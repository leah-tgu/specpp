package org.processmining.estminer.specpp.supervision.transformers;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.piping.ObservationTransformer;
import org.processmining.estminer.specpp.traits.Mergeable;

import java.util.function.Supplier;

public class AccumulatingTransformer<O extends Observation & Mergeable> implements ObservationTransformer<O, O> {

    private final O accumulator;

    public AccumulatingTransformer(Supplier<O> initial) {
        this.accumulator = initial.get();
    }

    @Override
    public O apply(O o) {
        accumulator.merge(o);
        return accumulator;
    }

}
