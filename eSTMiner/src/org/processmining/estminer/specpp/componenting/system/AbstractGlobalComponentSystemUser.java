package org.processmining.estminer.specpp.componenting.system;

import org.processmining.estminer.specpp.componenting.traits.UsesGlobalComponentSystem;

public abstract class AbstractGlobalComponentSystemUser implements UsesGlobalComponentSystem {

    private final ComponentCollection componentSystemAdapter;

    public AbstractGlobalComponentSystemUser() {
        componentSystemAdapter = new GlobalComponentRepository();
    }

    protected AbstractGlobalComponentSystemUser(ComponentCollection componentSystemAdapter) {
        this.componentSystemAdapter = componentSystemAdapter;
    }

    @Override
    public ComponentCollection componentSystemAdapter() {
        return componentSystemAdapter;
    }

    @Override
    public String toString() {
        return componentSystemAdapter.toString();
    }
}
