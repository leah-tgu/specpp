package org.processmining.estminer.specpp.datastructures.util;

import org.processmining.estminer.specpp.componenting.delegators.AbstractDelegator;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;

public class Button extends DelegatingDataSource<Runnable> {

    public void press() {
        if (delegate != null)
            delegate.getData().run();
    }

}
