package org.processmining.estminer.specpp.componenting.system;

import org.processmining.estminer.specpp.componenting.traits.UsesGlobalComponentSystem;

public abstract class AbstractGlobalComponentSystemUser implements UsesGlobalComponentSystem {

    private final GlobalComponentRepository componentSystemAdapter;

    public AbstractGlobalComponentSystemUser() {
        componentSystemAdapter = new GlobalComponentRepository();
    }

    protected AbstractGlobalComponentSystemUser(GlobalComponentRepository componentSystemAdapter) {
        this.componentSystemAdapter = componentSystemAdapter;
    }

    @Override
    public GlobalComponentRepository componentSystemAdapter() {
        return componentSystemAdapter;
    }

    @Override
    public String toString() {
        return componentSystemAdapter.toString();
    }
}
