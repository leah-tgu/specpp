package org.processmining.estminer.specpp.supervision.instrumentators;

import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.estminer.specpp.componenting.system.link.PostProcessorComponent;
import org.processmining.estminer.specpp.componenting.traits.UsesGlobalComponentSystem;
import org.processmining.estminer.specpp.config.SimpleBuilder;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;

public class InstrumentedPostProcessor<R extends Result, F extends Result> extends AbstractInstrumentingDelegator<PostProcessorComponent<R, F>> implements PostProcessorComponent<R, F> {

    private final TaskDescription task;

    public InstrumentedPostProcessor(String label, PostProcessorComponent<R, F> postProcessor) {
        super(postProcessor);
        String fullLabel = "postprocessor." + label;
        task = new TaskDescription(fullLabel);
        globalComponentSystem().provide(SupervisionRequirements.observable(fullLabel + ".performance", PerformanceEvent.class, timeStopper));
    }

    public static class Builder<R extends Result, F extends Result> extends ComponentSystemAwareBuilder<InstrumentedPostProcessor<R, F>> {

        private final String label;
        private final SimpleBuilder<PostProcessorComponent<R, F>> inner;

        public Builder(String label, SimpleBuilder<PostProcessorComponent<R, F>> inner) {
            this.label = label;
            this.inner = inner;
            if (inner instanceof UsesGlobalComponentSystem)
                globalComponentSystem().consumeEntirely(((UsesGlobalComponentSystem) inner).globalComponentSystem());
        }

        @Override
        public InstrumentedPostProcessor<R, F> buildIfFullySatisfied() {
            return new InstrumentedPostProcessor<>(label, inner.build());
        }
    }

    @Override
    public F postProcess(R result) {
        timeStopper.start(task);
        F f = delegate.postProcess(result);
        timeStopper.stop(task);
        return f;
    }
}
