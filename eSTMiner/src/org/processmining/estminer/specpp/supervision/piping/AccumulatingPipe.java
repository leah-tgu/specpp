package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.traits.OneToOne;
import org.processmining.estminer.specpp.supervision.transformers.AccumulatingTransformer;
import org.processmining.estminer.specpp.traits.Mergeable;

import java.util.function.Supplier;

public class AccumulatingPipe<O extends Observation & Mergeable<? super O>> extends TypeIdentTransformingPipe<O> implements OneToOne<O, O> {
    public AccumulatingPipe(Supplier<O> initial) {
        super(new AccumulatingTransformer<>(initial));
    }

}
