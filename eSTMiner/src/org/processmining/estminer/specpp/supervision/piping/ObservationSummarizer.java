package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.Observation;

@FunctionalInterface
public interface ObservationSummarizer<I extends Observation, O extends Observation> extends ObservationTransformer<Observations<I>, O> {

    O summarize(Observations<? extends I> observations);


    @Override
    default O apply(Observations<I> os) {
        return summarize(os);
    }
}
