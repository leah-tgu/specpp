package org.processmining.estminer.specpp.supervision.instrumentators;

import org.processmining.estminer.specpp.componenting.delegators.AbstractFCSUDelegator;
import org.processmining.estminer.specpp.componenting.system.FullComponentSystemUser;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;

public class AbstractInstrumentingDelegator<T extends FullComponentSystemUser> extends AbstractFCSUDelegator<T> {


    protected final TimeStopper timeStopper = new TimeStopper();

    public AbstractInstrumentingDelegator(T delegate) {
        super(delegate);
    }

}
