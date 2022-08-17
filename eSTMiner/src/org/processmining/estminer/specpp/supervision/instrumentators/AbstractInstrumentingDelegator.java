package org.processmining.estminer.specpp.supervision.instrumentators;

import org.processmining.estminer.specpp.componenting.delegators.AbstractDelegator;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.componenting.traits.UsesComponentSystem;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;

public class AbstractInstrumentingDelegator<T> extends AbstractDelegator<T> implements UsesComponentSystem {


    protected final TimeStopper timeStopper = new TimeStopper();
    private final ComponentSystemAdapter componentSystemAdapter;

    public AbstractInstrumentingDelegator(T delegate) {
        super(delegate);
        this.componentSystemAdapter = new ComponentSystemAdapter();
        if (delegate instanceof UsesComponentSystem) {
            componentSystemAdapter.consumeEntirely(((UsesComponentSystem) delegate).componentSystemAdapter());
        }
    }

    @Override
    public ComponentSystemAdapter componentSystemAdapter() {
        return componentSystemAdapter;
    }

}
