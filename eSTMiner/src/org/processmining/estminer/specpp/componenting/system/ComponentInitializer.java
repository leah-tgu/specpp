package org.processmining.estminer.specpp.componenting.system;

import org.processmining.estminer.specpp.componenting.traits.HasComponentCollection;
import org.processmining.estminer.specpp.componenting.traits.IsGlobalProvider;
import org.processmining.estminer.specpp.componenting.traits.ProvisionsComponents;
import org.processmining.estminer.specpp.componenting.traits.RequiresComponents;

public class ComponentInitializer extends AbstractGlobalComponentSystemUser {


    public ComponentInitializer(ComponentCollection componentCollection) {
        super(componentCollection);
    }

    public <T> T checkout(T other) {
        if (other instanceof RequiresComponents || other instanceof FulfilledRequirementsCollection || other instanceof ProvisionsComponents) {
            if (other instanceof RequiresComponents) {
                RequiresComponents requiresComponents = (RequiresComponents) other;
                getComponentCollection().fulfil(requiresComponents);
            }
            if (other instanceof FulfilledRequirementsCollection) {
                FulfilledRequirementsCollection<?> frp = (FulfilledRequirementsCollection<?>) other;
                getComponentCollection().fulfilFrom(frp);
                if (frp instanceof IsGlobalProvider) getComponentCollection().absorb(frp);
            }
            if (other instanceof ProvisionsComponents) {
                ProvisionsComponents provisionsComponents = (ProvisionsComponents) other;
                for (FulfilledRequirementsCollection<?> frp : provisionsComponents.componentProvisions().values()) {
                    getComponentCollection().fulfilFrom(frp);
                    if (frp instanceof IsGlobalProvider) getComponentCollection().absorb(frp);
                }
            }
        } else if (other instanceof HasComponentCollection) {
            checkout(((HasComponentCollection) other).getComponentCollection());
        }
        if (other instanceof IsGlobalProvider) absorb(other);
        return other;
    }

    public <T> void absorb(T other) {
        if (other instanceof FulfilledRequirementsCollection) {
            getComponentCollection().absorb((FulfilledRequirementsCollection<?>) other);
        } else if (other instanceof ProvisionsComponents) {
            getComponentCollection().absorb((ProvisionsComponents) other);
        } else if (other instanceof HasComponentCollection) {
            absorb(((HasComponentCollection) other).getComponentCollection());
        }
    }

    public <T> void overridingAbsorb(T other) {
        if (other instanceof FulfilledRequirementsCollection) {
            getComponentCollection().overridingAbsorb((FulfilledRequirementsCollection<?>) other);
        } else if (other instanceof ProvisionsComponents) {
            getComponentCollection().overridingAbsorb((ProvisionsComponents) other);
        } else if (other instanceof HasComponentCollection) {
            overridingAbsorb(((HasComponentCollection) other).getComponentCollection());
        }
    }

    public <T> T checkoutAndAbsorb(T other) {
        checkout(other);
        absorb(other);
        return other;
    }

}
