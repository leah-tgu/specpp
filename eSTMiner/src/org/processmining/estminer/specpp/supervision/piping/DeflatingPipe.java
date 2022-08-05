package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.traits.ManyToOne;

public class DeflatingPipe<I extends Observation, O extends Observation> extends TransformingPipe<Observations<I>, O> implements ManyToOne<I, O> {
    public DeflatingPipe(ObservationSummarizer<I, O> summarizer) {
        super(summarizer);
    }

}
