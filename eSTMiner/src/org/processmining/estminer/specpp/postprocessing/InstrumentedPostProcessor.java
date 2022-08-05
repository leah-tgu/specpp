package org.processmining.estminer.specpp.postprocessing;

import org.processmining.estminer.specpp.base.PostProcessor;
import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.AbstractComponentSystemUser;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.estminer.specpp.componenting.traits.UsesComponentSystem;
import org.processmining.estminer.specpp.config.SimpleBuilder;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;

public class InstrumentedPostProcessor<R extends Result, F extends Result> extends AbstractComponentSystemUser implements PostProcessor<R, F> {

    private final TimeStopper timeStopper = new TimeStopper();
    private final TaskDescription task;
    private final PostProcessor<R, F> postProcessor;

    public InstrumentedPostProcessor(String label, PostProcessor<R, F> postProcessor) {
        this.postProcessor = postProcessor;
        String fullLabel = "postprocessor." + label;
        task = new TaskDescription(fullLabel);
        componentSystemAdapter().provide(SupervisionRequirements.observable(fullLabel + ".performance", PerformanceEvent.class, timeStopper));
        if (postProcessor instanceof UsesComponentSystem) {
            componentSystemAdapter().consumeEntirely(((UsesComponentSystem) postProcessor).componentSystemAdapter());
        }
    }

    public static class Builder<R extends Result, F extends Result> extends ComponentSystemAwareBuilder<InstrumentedPostProcessor<R, F>> {

        private final String label;
        private final SimpleBuilder<PostProcessor<R, F>> inner;

        public Builder(String label, SimpleBuilder<PostProcessor<R, F>> inner) {
            this.label = label;
            this.inner = inner;
            if (inner instanceof UsesComponentSystem)
                componentSystemAdapter().consumeEntirely(((UsesComponentSystem) inner).componentSystemAdapter());
        }

        @Override
        public InstrumentedPostProcessor<R, F> buildIfFullySatisfied() {
            return new InstrumentedPostProcessor<>(label, inner.build());
        }
    }

    @Override
    public F postProcess(R result) {
        timeStopper.start(task);
        F f = postProcessor.postProcess(result);
        timeStopper.stop(task);
        return f;
    }
}
