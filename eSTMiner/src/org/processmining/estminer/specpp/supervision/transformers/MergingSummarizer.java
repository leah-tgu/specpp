package org.processmining.estminer.specpp.supervision.transformers;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.piping.ObservationSummarizer;
import org.processmining.estminer.specpp.supervision.piping.Observations;
import org.processmining.estminer.specpp.traits.Mergeable;

public class MergingSummarizer<O extends Observation & Mergeable<? super O>> implements ObservationSummarizer<O, O> {

    @Override
    public O summarize(Observations<? extends O> observations) {
        O accumulator = null;
        for (O observation : observations) {
            if (accumulator == null) accumulator = observation;
            else accumulator.merge(observation);
        }
        return accumulator;
    }

}
