package org.processmining.estminer.specpp.componenting.delegators;

import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.system.LocalComponentRepository;
import org.processmining.estminer.specpp.componenting.traits.UsesLocalComponentSystem;
import org.processmining.estminer.specpp.traits.Initializable;

public abstract class AbstractLocalComponentSystemAwareDelegator<T> extends AbstractDelegator<T> implements UsesLocalComponentSystem, Initializable {

    protected LocalComponentRepository lcr = new LocalComponentRepository();

    public AbstractLocalComponentSystemAwareDelegator(T delegate) {
        super(delegate);
    }

    @Override
    public ComponentCollection localComponentSystem() {
        return lcr;
    }

    @Override
    public void bridgeToChildren() {
        UsesLocalComponentSystem.bridgeTheGap(this, delegate);
    }

    @Override
    public void init() {
        if (delegate instanceof Initializable) ((Initializable) delegate).init();
    }
}
