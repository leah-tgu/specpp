package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.transformers.MergingSummarizer;
import org.processmining.estminer.specpp.traits.Mergeable;

public class MergingSummarizingPipe<O extends Observation & Mergeable<? super O>> extends SummarizingPipe<O> {
    public MergingSummarizingPipe() {
        super(new MergingSummarizer<>());
    }
}
