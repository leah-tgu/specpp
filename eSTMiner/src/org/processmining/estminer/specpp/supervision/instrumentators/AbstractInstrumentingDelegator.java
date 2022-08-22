package org.processmining.estminer.specpp.supervision.instrumentators;

import org.processmining.estminer.specpp.componenting.delegators.AbstractLocalComponentSystemAwareDelegator;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.componenting.traits.HasComponentCollection;
import org.processmining.estminer.specpp.componenting.traits.UsesGlobalComponentSystem;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;

public class AbstractInstrumentingDelegator<T> extends AbstractLocalComponentSystemAwareDelegator<T> implements UsesGlobalComponentSystem {


    protected final TimeStopper timeStopper = new TimeStopper();
    private final GlobalComponentRepository gcr = new GlobalComponentRepository();

    public AbstractInstrumentingDelegator(T delegate) {
        super(delegate);
        if (delegate instanceof UsesGlobalComponentSystem) {
            gcr.consumeEntirely(((HasComponentCollection) delegate).getComponentCollection());
        }
    }

    @Override
    public ComponentCollection componentSystemAdapter() {
        return gcr;
    }


    @Override
    public ComponentCollection getComponentCollection() {
        return gcr;
    }
}
