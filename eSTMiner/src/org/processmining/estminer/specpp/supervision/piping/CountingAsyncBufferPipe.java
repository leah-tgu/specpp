package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.Observation;

public class CountingAsyncBufferPipe<O extends Observation> extends AsyncBufferPipe<O> {

    private final int threshold;

    public CountingAsyncBufferPipe(int threshold) {
        this.threshold = threshold;
    }

    @Override
    protected void buffer(O observation) {
        super.buffer(observation);
        if (buffer.size() >= threshold) trigger();
    }
}
