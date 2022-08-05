package org.processmining.estminer.specpp.componenting.delegators;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.piping.Observer;

public class DelegatingObserver<O extends Observation> extends AbstractDelegator<Observer<O>> implements Observer<O> {

    public DelegatingObserver() {
        delegate = o -> {
        };
    }

    public DelegatingObserver(Observer<O> delegate) {
        super(delegate);
    }

    @Override
    public void observe(O observation) {
        delegate.observe(observation);
    }


}
