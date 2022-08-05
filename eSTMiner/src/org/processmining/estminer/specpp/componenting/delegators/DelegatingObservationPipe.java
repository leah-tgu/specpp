package org.processmining.estminer.specpp.componenting.delegators;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.piping.ObservationPipe;
import org.processmining.estminer.specpp.supervision.piping.Observer;

import java.util.Collection;

public class DelegatingObservationPipe<O extends Observation, C extends Observation> extends AbstractDelegator<ObservationPipe<O, C>> implements ObservationPipe<O, C> {

    public DelegatingObservationPipe() {
    }

    public DelegatingObservationPipe(ObservationPipe<O, C> delegate) {
        super(delegate);
    }

    public void addObserver(Observer<C> observer) {
        delegate.addObserver(observer);
    }

    public Collection<Observer<C>> getObservers() {
        return delegate.getObservers();
    }

    public void removeObserver(Observer<C> observer) {
        delegate.removeObserver(observer);
    }

    public void clearObservers() {
        delegate.clearObservers();
    }

    public void publish(C observation) {
        delegate.publish(observation);
    }

    public void observe(O observation) {
        delegate.observe(observation);
    }
}
