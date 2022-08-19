package org.processmining.estminer.specpp.supervision.instrumentators;

import org.processmining.estminer.specpp.componenting.delegators.AbstractDelegator;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.componenting.traits.HasComponentCollection;
import org.processmining.estminer.specpp.componenting.traits.UsesGlobalComponentSystem;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;

public class AbstractInstrumentingDelegator<T> extends AbstractDelegator<T> implements UsesGlobalComponentSystem {


    protected final TimeStopper timeStopper = new TimeStopper();
    private final ComponentCollection componentSystemAdapter;

    public AbstractInstrumentingDelegator(T delegate) {
        super(delegate);
        this.componentSystemAdapter = new GlobalComponentRepository();
        if (delegate instanceof UsesGlobalComponentSystem) {
            componentSystemAdapter.consumeEntirely(((HasComponentCollection) delegate).getComponentCollection());
        }
    }

    @Override
    public ComponentCollection componentSystemAdapter() {
        return componentSystemAdapter;
    }

}
