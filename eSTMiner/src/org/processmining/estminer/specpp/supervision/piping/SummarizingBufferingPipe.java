package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.Observation;

public class SummarizingBufferingPipe<I extends Observation, O extends Observation> extends AbstractBufferingPipe<I, O> {

    private final ObservationSummarizer<? super I, ? extends O> collator;

    public SummarizingBufferingPipe(ObservationSummarizer<? super I, ? extends O> summarizer) {
        this(summarizer, false);
    }

    protected SummarizingBufferingPipe(ObservationSummarizer<? super I, ? extends O> summarizer, boolean useConcurrentBuffer) {
        super(useConcurrentBuffer);
        this.collator = summarizer;
    }

    @Override
    protected O collect(Observations<I> bufferedObservations) {
        return collator.summarize(bufferedObservations);
    }

}
