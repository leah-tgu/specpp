package org.processmining.estminer.specpp.componenting.traits;

import org.processmining.estminer.specpp.componenting.system.ComponentCollection;

public interface UsesGlobalComponentSystem extends HasComponentCollection, IsGlobalProvider {

    ComponentCollection componentSystemAdapter();

    @Override
    default ComponentCollection getComponentCollection() {
        return componentSystemAdapter();
    }

}
