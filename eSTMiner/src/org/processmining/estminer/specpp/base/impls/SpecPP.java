package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.*;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.componenting.traits.UsesLocalComponentSystem;
import org.processmining.estminer.specpp.config.*;
import org.processmining.estminer.specpp.supervision.Supervisor;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.supervision.piping.LayingPipe;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;
import org.processmining.estminer.specpp.supervision.supervisors.DebuggingSupervisor;
import org.processmining.estminer.specpp.traits.Initializable;
import org.processmining.estminer.specpp.traits.Joinable;
import org.processmining.estminer.specpp.traits.StartStoppable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SpecPP<C extends Candidate, I extends Composition<C>, R extends Result, F extends Result> extends AbstractGlobalComponentSystemUser implements Initializable, StartStoppable {

    public static final TaskDescription PEC_CYCLE = new TaskDescription("PEC Cycle");
    public static final TaskDescription TOTAL_CYCLING = new TaskDescription("Total PEC Cycling");
    private final GlobalComponentRepository cr;
    private final List<Supervisor> supervisors;

    private final Proposer<C> proposer;
    private final Composer<C, I, R> composer;
    private final PostProcessor<R, F> postProcessor;
    private final Configuration configuration;

    private int stepCount = 0;
    private R result;
    private F finalResult;


    private final TimeStopper timeStopper = new TimeStopper();

    public SpecPP(GlobalComponentRepository cr, List<Supervisor> supervisors, Proposer<C> proposer, Composer<C, I, R> composer, PostProcessor<R, F> postProcessor) {
        this.cr = cr;
        this.supervisors = supervisors;
        this.proposer = proposer;
        this.composer = composer;
        this.postProcessor = postProcessor;
        configuration = new Configuration(cr);

        componentSystemAdapter().provide(SupervisionRequirements.observable("pec.performance", PerformanceEvent.class, timeStopper));
    }

    protected static void linkConstraintsIfPossible(Composer<?, ?, ?> composer, Proposer<?> proposer) {
        if (composer instanceof Constrainer && proposer instanceof Constrainable) {
            Constrainable<?> constrainable = (Constrainable<?>) proposer;
            Class<?> acceptedConstraintClass = constrainable.getAcceptedConstraintClass();
            Constrainer<?> constrainer = (Constrainer<?>) composer;
            Class<?> constraintClass = constrainer.getPublishedConstraintClass();
            if (acceptedConstraintClass.isAssignableFrom(constraintClass))
                LayingPipe.link(constrainer.getConstraintPublisher(), constrainable);
        }
    }

    public GlobalComponentRepository getComponentRepository() {
        return cr;
    }

    public static class Builder<C extends Candidate, I extends Composition<C>, R extends Result, F extends Result> extends AbstractGlobalComponentSystemUser implements InitializingBuilder<SpecPP<C, I, R, F>, GlobalComponentRepository> {

        private final DelegatingDataSource<ProposerComposerConfiguration<C, I, R>> pcConfigDelegator = DataRequirements.<C, I, R>proposerComposerConfiguration()
                                                                                                                       .emptyDelegator();
        private final DelegatingDataSource<PostProcessingConfiguration<R, F>> ppConfigDelegator = DataRequirements.<R, F>postprocessingConfiguration()
                                                                                                                  .emptyDelegator();
        private final DelegatingDataSource<SupervisionConfiguration> svConfigDelegator = DataRequirements.SUPERVISOR_CONFIG.emptyDelegator();
        private final DelegatingDataSource<EvaluatorConfiguration> evConfigDelegator = DataRequirements.EVALUATOR_CONFIG.emptyDelegator();


        public Builder() {
            componentSystemAdapter().require(DataRequirements.proposerComposerConfiguration(), pcConfigDelegator)
                                    .require(DataRequirements.postprocessingConfiguration(), ppConfigDelegator)
                                    .require(DataRequirements.EVALUATOR_CONFIG, evConfigDelegator)
                                    .require(DataRequirements.SUPERVISOR_CONFIG, svConfigDelegator);
        }

        @Override
        public SpecPP<C, I, R, F> build(GlobalComponentRepository cr) {
            SupervisionConfiguration svConfig = svConfigDelegator.getData();
            List<Supervisor> supervisorList = svConfig.createSupervisors();
            ProposerComposerConfiguration<C, I, R> pcConfig = pcConfigDelegator.getData();
            PostProcessingConfiguration<R, F> ppConfig = ppConfigDelegator.getData();
            EvaluatorConfiguration evConfig = evConfigDelegator.getData();
            evConfig.createEvaluators();
            return new SpecPP<>(cr, supervisorList, pcConfig.createProposer(), pcConfig.createComposer(), ppConfig.createPostProcessorPipeline());
        }
    }

    public R getResult() {
        return result;
    }

    public F getFinalResult() {
        return finalResult;
    }

    @Override
    public void init() {
        configuration.checkoutAndAbsorb(composer);
        configuration.checkoutAndAbsorb(proposer);
        configuration.checkoutAndAbsorb(postProcessor);

        for (Supervisor supervisor : supervisors) {
            configuration.checkout(supervisor);
            supervisor.init();
            configuration.absorb(supervisor);
        }

        if (proposer instanceof Initializable) ((Initializable) proposer).init();
        if (composer instanceof Initializable) ((Initializable) composer).init();
        if (postProcessor instanceof Initializable) ((Initializable) postProcessor).init();
        UsesLocalComponentSystem.bridgeTheGap(proposer, composer, false);

        DebuggingSupervisor.debug("specpp init", ((UsesLocalComponentSystem) proposer).localComponentSystem());
        DebuggingSupervisor.debug("specpp init", ((UsesLocalComponentSystem) composer).localComponentSystem());


        for (Supervisor supervisor : supervisors) {
            configuration.absorb(supervisor);
        }
    }

    @Override
    public void start() {
        supervisors.forEach(Supervisor::start);
    }


    public boolean executePECCycle() {
        if (composer.isFinished()) return true;
        C c = proposer.proposeCandidate();
        if (c == null) return true;
        composer.accept(c);
        return false;
    }

    protected boolean executePECCycleInstrumented() {
        timeStopper.start(PEC_CYCLE);
        boolean stop = executePECCycle();
        timeStopper.stop(PEC_CYCLE);
        return stop;
    }

    protected void executeAllPECCycles() {
        while (!executePECCycleInstrumented()) ++stepCount;
    }

    protected void executeAllPECCyclesInstrumented() {
        timeStopper.start(TOTAL_CYCLING);
        executeAllPECCycles();
        timeStopper.stop(TOTAL_CYCLING);
    }

    protected void generateResult() {
        result = composer.generateResult();
    }

    protected void postProcess() {
        finalResult = postProcessor.postProcess(result);
    }

    public F executeAll() {
        executeAllPECCyclesInstrumented();
        generateResult();
        postProcess();
        return finalResult;
    }

    public CompletableFuture<F> future(Executor executor) {
        return CompletableFuture.supplyAsync(this::executeAll, executor);
    }

    public Proposer<C> getProposer() {
        return proposer;
    }

    public Composer<C, I, R> getComposer() {
        return composer;
    }

    public int stepCount() {
        return stepCount;
    }

    @Override
    public void stop() {
        supervisors.forEach(Supervisor::stop);
        for (Supervisor supervisor : supervisors) {
            if (supervisor instanceof Joinable) {
                try {
                    ((Joinable) supervisor).join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public Collection<Supervisor> getSupervisors() {
        return supervisors;
    }


}
