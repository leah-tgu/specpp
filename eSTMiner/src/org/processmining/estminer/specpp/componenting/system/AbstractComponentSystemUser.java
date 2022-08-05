package org.processmining.estminer.specpp.componenting.system;

import org.processmining.estminer.specpp.componenting.traits.UsesComponentSystem;

public abstract class AbstractComponentSystemUser implements UsesComponentSystem {

    protected final ComponentSystemAdapter componentSystemAdapter;

    public AbstractComponentSystemUser() {
        componentSystemAdapter = new ComponentSystemAdapter();
    }

    protected AbstractComponentSystemUser(ComponentSystemAdapter componentSystemAdapter) {
        this.componentSystemAdapter = componentSystemAdapter;
    }

    @Override
    public ComponentSystemAdapter componentSystemAdapter() {
        return componentSystemAdapter;
    }

    @Override
    public String toString() {
        return componentSystemAdapter.toString();
    }
}
