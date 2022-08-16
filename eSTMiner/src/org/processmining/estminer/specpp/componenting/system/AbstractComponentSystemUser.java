package org.processmining.estminer.specpp.componenting.system;

import org.apache.commons.math3.ode.nonstiff.AdaptiveStepsizeFieldIntegrator;
import org.processmining.estminer.specpp.componenting.traits.UsesComponentSystem;

public abstract class AbstractComponentSystemUser implements UsesComponentSystem {

    private final ComponentSystemAdapter componentSystemAdapter;

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
