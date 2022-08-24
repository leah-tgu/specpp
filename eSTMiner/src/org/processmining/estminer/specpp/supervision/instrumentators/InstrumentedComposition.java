package org.processmining.estminer.specpp.supervision.instrumentators;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.componenting.system.link.CompositionComponent;

import java.util.Iterator;
import java.util.Set;

public class InstrumentedComposition<C extends Candidate> extends AbstractInstrumentingDelegator<CompositionComponent<C>> implements CompositionComponent<C> {

    public InstrumentedComposition(CompositionComponent<C> delegate) {
        super(delegate);
    }


    @Override
    public Iterator<C> iterator() {
        return delegate.iterator();
    }

    @Override
    public void accept(C c) {
        delegate.accept(c);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean hasCapacityLeft() {
        return delegate.hasCapacityLeft();
    }

    @Override
    public Set<C> toSet() {
        return delegate.toSet();
    }


}
