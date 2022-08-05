package org.processmining.estminer.specpp.componenting.system;

import org.processmining.estminer.specpp.componenting.system.AbstractComponentSystemUser;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.componenting.system.FulfilledRequirementsCollection;
import org.processmining.estminer.specpp.componenting.traits.IsGlobalProvider;
import org.processmining.estminer.specpp.componenting.traits.ProvisionsComponents;
import org.processmining.estminer.specpp.componenting.traits.RequiresComponents;
import org.processmining.estminer.specpp.componenting.traits.UsesComponentSystem;

public class ComponentInitializer extends AbstractComponentSystemUser {


    public ComponentInitializer(ComponentSystemAdapter componentSystemAdapter) {
        super(componentSystemAdapter);
    }

    public <T> T checkout(T other) {
        if (other instanceof RequiresComponents || other instanceof FulfilledRequirementsCollection || other instanceof ProvisionsComponents) {
            if (other instanceof RequiresComponents) {
                RequiresComponents requiresComponents = (RequiresComponents) other;
                componentSystemAdapter.fulfil(requiresComponents);
            }
            if (other instanceof FulfilledRequirementsCollection) {
                FulfilledRequirementsCollection<?> frp = (FulfilledRequirementsCollection<?>) other;
                componentSystemAdapter.fulfilFrom(frp);
                if (frp instanceof IsGlobalProvider) componentSystemAdapter.absorb(frp);
            }
            if (other instanceof ProvisionsComponents) {
                ProvisionsComponents provisionsComponents = (ProvisionsComponents) other;
                for (FulfilledRequirementsCollection<?> frp : provisionsComponents.componentProvisions().values()) {
                    componentSystemAdapter.fulfilFrom(frp);
                    if (frp instanceof IsGlobalProvider) componentSystemAdapter.absorb(frp);
                }
            }
        } else if (other instanceof UsesComponentSystem) {
            checkout(((UsesComponentSystem) other).componentSystemAdapter());
        }
        if (other instanceof IsGlobalProvider) absorb(other);
        return other;
    }

    public <T> void absorb(T other) {
        if (other instanceof FulfilledRequirementsCollection) {
            componentSystemAdapter.absorb((FulfilledRequirementsCollection<?>) other);
        } else if (other instanceof ProvisionsComponents) {
            componentSystemAdapter.absorb((ProvisionsComponents) other);
        } else if (other instanceof UsesComponentSystem) {
            absorb(((UsesComponentSystem) other).componentSystemAdapter());
        }
    }

    public <T> T checkoutAndAbsorb(T other) {
        checkout(other);
        absorb(other);
        return other;
    }

}
