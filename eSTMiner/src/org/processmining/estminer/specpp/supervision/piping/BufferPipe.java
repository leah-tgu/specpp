package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.traits.OneToMany;

public class BufferPipe<O extends Observation> extends AbstractBufferingPipe<O, Observations<O>> implements OneToMany<O, O> {

    public BufferPipe() {
        super(false);
    }

    public BufferPipe(boolean useConcurrentBuffer) {
        super(useConcurrentBuffer);
    }

    @Override
    protected Observations<O> collect(Observations<O> bufferedObservations) {
        return bufferedObservations;
    }

}
