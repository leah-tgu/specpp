package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.*;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.componenting.system.LocalComponentRepository;
import org.processmining.estminer.specpp.componenting.system.link.AbstractBaseClass;
import org.processmining.estminer.specpp.componenting.system.link.ComposerComponent;
import org.processmining.estminer.specpp.componenting.system.link.CompositionComponent;
import org.processmining.estminer.specpp.componenting.system.link.ProposerComponent;
import org.processmining.estminer.specpp.config.*;
import org.processmining.estminer.specpp.supervision.Supervisor;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;
import org.processmining.estminer.specpp.supervision.supervisors.DebuggingSupervisor;
import org.processmining.estminer.specpp.traits.Joinable;
import org.processmining.estminer.specpp.traits.StartStoppable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SpecPP<C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> extends AbstractBaseClass implements StartStoppable {

    public static class Builder<C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> extends AbstractGlobalComponentSystemUser implements InitializingBuilder<SpecPP<C, I, R, F>, GlobalComponentRepository> {

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
        public SpecPP<C, I, R, F> build(GlobalComponentRepository gcr) {
            SupervisionConfiguration svConfig = svConfigDelegator.getData();
            List<Supervisor> supervisorList = svConfig.createSupervisors();
            for (Supervisor supervisor : supervisorList) {
                gcr.consumeEntirely(supervisor.componentSystemAdapter());
            }
            ProposerComposerConfiguration<C, I, R> pcConfig = pcConfigDelegator.getData();
            PostProcessingConfiguration<R, F> ppConfig = ppConfigDelegator.getData();
            EvaluatorConfiguration evConfig = evConfigDelegator.getData();
            evConfig.createEvaluators();
            return new SpecPP<>(gcr, supervisorList, pcConfig.createProposer(), pcConfig.createComposer(), ppConfig.createPostProcessorPipeline());
        }
    }

    public static final TaskDescription PEC_CYCLE = new TaskDescription("PEC Cycle");
    public static final TaskDescription TOTAL_CYCLING = new TaskDescription("Total PEC Cycling");
    private final GlobalComponentRepository cr;

    private final List<Supervisor> supervisors;
    private final ProposerComponent<C> proposer;
    private final ComposerComponent<C, I, R> composer;
    private final PostProcessor<R, F> postProcessor;

    private final Configuration configuration;
    private int stepCount = 0;
    private R result;

    private F finalResult;


    private final TimeStopper timeStopper = new TimeStopper();

    public SpecPP(GlobalComponentRepository cr, List<Supervisor> supervisors, ProposerComponent<C> proposer, ComposerComponent<C, I, R> composer, PostProcessor<R, F> postProcessor) {
        this.cr = cr;
        this.supervisors = supervisors;
        this.proposer = proposer;
        this.composer = composer;
        this.postProcessor = postProcessor;
        configuration = new Configuration(cr);

        componentSystemAdapter().provide(SupervisionRequirements.observable("pec.performance", PerformanceEvent.class, timeStopper));

        registerSubComponent(proposer);
        registerSubComponent(composer);
    }

    public GlobalComponentRepository getGlobalComponentRepository() {
        return cr;
    }

    public R getResult() {
        return result;
    }

    public F getFinalResult() {
        return finalResult;
    }


    @Override
    protected void preSubComponentInit() {
        for (Supervisor supervisor : supervisors) {
            configuration.checkout(supervisor);
            supervisor.init();
            configuration.absorbProvisions(supervisor);
        }
        LocalComponentRepository proposerLcr = new LocalComponentRepository();
        LocalComponentRepository composerLcr = new LocalComponentRepository();
        proposer.connectLocalComponentSystem(proposerLcr);
        composer.connectLocalComponentSystem(composerLcr);
        proposerLcr.fulfil(composerLcr);
        composerLcr.fulfil(proposerLcr);
        DebuggingSupervisor.debug("specpp init", proposerLcr);
        DebuggingSupervisor.debug("specpp init", composerLcr);
    }

    @Override
    public void initSelf() {

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
