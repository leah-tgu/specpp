package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.*;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.AbstractComponentSystemUser;
import org.processmining.estminer.specpp.componenting.system.ComponentRepository;
import org.processmining.estminer.specpp.config.InitializingBuilder;
import org.processmining.estminer.specpp.config.PostProcessingConfiguration;
import org.processmining.estminer.specpp.config.ProposerComposerConfiguration;
import org.processmining.estminer.specpp.config.SupervisionConfiguration;
import org.processmining.estminer.specpp.supervision.Supervisor;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.supervision.piping.LayingPipe;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;
import org.processmining.estminer.specpp.traits.Initializable;
import org.processmining.estminer.specpp.traits.Joinable;
import org.processmining.estminer.specpp.traits.StartStoppable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SpecPP<C extends Candidate, I extends Composition<C>, R extends Result, F extends Result> extends AbstractComponentSystemUser implements Initializable, StartStoppable {

    private final ComponentRepository cr;
    private final List<Supervisor> supervisors;

    private final Proposer<C> proposer;
    private final Composer<C, I, R> composer;
    private final PostProcessor<R, F> postProcessor;

    private int stepCount = 0;
    private R result;
    private F finalResult;


    private final TimeStopper timeStopper = new TimeStopper();

    public SpecPP(ComponentRepository cr, List<Supervisor> supervisors, Proposer<C> proposer, Composer<C, I, R> composer, PostProcessor<R, F> postProcessor) {
        this.cr = cr;
        this.supervisors = supervisors;
        this.proposer = proposer;
        this.composer = composer;
        this.postProcessor = postProcessor;

        linkConstraintsIfPossible(composer, proposer);

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

    public ComponentRepository getComponentRepository() {
        return cr;
    }

    public static class Builder<C extends Candidate, I extends Composition<C>, R extends Result, F extends Result> extends AbstractComponentSystemUser implements InitializingBuilder<SpecPP<C, I, R, F>, ComponentRepository> {

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
        public SpecPP<C, I, R, F> build(ComponentRepository cr) {
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
        cr.checkoutAndAbsorb(composer);
        cr.checkoutAndAbsorb(proposer);
        cr.checkoutAndAbsorb(postProcessor);

        for (Supervisor supervisor : supervisors) {
            cr.checkout(supervisor);
            supervisor.init();
            cr.absorb(supervisor);
        }

        if (proposer instanceof Initializable) ((Initializable) proposer).init();
        if (composer instanceof Initializable) ((Initializable) composer).init();
        if (postProcessor instanceof Initializable) ((Initializable) postProcessor).init();

        for (Supervisor supervisor : supervisors) {
            cr.absorb(supervisor);
        }
    }

    @Override
    public void start() {
        supervisors.forEach(Supervisor::start);
    }


    public boolean step() {
        timeStopper.start(TaskDescription.PEC_CYCLE);
        boolean notFinished = true;
        if (!proposer.isExhausted() && !composer.isFinished()) {
            C c = proposer.proposeCandidate();
            composer.accept(c);
        } else notFinished = false;
        timeStopper.stop(TaskDescription.PEC_CYCLE);
        return notFinished;
    }

    protected void stepThrough() {
        timeStopper.start(TaskDescription.TOTAL_CYCLING);
        while (step()) ++stepCount;
        timeStopper.stop(TaskDescription.TOTAL_CYCLING);
    }

    protected void generateResult() {
        result = composer.generateResult();
    }

    protected void postProcess() {
        finalResult = postProcessor.postProcess(result);
    }


    public CompletableFuture<F> future(Executor executor) {
        return CompletableFuture.runAsync(this::stepThrough, executor)
                                .thenRun(this::generateResult)
                                .thenRun(this::postProcess)
                                .thenApply(o -> finalResult);
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
