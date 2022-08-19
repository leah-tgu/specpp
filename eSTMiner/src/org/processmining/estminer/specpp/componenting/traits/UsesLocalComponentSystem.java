package org.processmining.estminer.specpp.componenting.traits;

import org.processmining.estminer.specpp.componenting.system.ComponentCollection;

public interface UsesLocalComponentSystem extends HasComponentCollection {

    ComponentCollection localComponentSystem();

    @Override
    default ComponentCollection getComponentCollection() {
        return localComponentSystem();
    }
}
