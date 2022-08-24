package org.processmining.estminer.specpp.componenting.delegators;

import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.system.FullComponentSystemUser;
import org.processmining.estminer.specpp.componenting.system.LocalComponentRepository;

import java.util.List;

public abstract class AbstractFCSUDelegator<T extends FullComponentSystemUser> extends AbstractDelegator<T> implements FullComponentSystemUser {


    public AbstractFCSUDelegator(T delegate) {
        super(delegate);
    }

    @Override
    public void init() {
        delegate.init();
    }

    public void registerSubComponent(FullComponentSystemUser subComponent) {
        delegate.registerSubComponent(subComponent);
    }

    public List<FullComponentSystemUser> collectTransitiveSubcomponents() {
        return delegate.collectTransitiveSubcomponents();
    }

    public void connectLocalComponentSystem(LocalComponentRepository lcr) {
        delegate.connectLocalComponentSystem(lcr);
    }

    public ComponentCollection getComponentCollection() {
        return delegate.getComponentCollection();
    }

    public ComponentCollection componentSystemAdapter() {
        return delegate.componentSystemAdapter();
    }

    public ComponentCollection localComponentSystem() {
        return delegate.localComponentSystem();
    }

}
