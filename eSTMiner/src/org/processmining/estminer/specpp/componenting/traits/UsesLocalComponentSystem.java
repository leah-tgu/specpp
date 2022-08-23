package org.processmining.estminer.specpp.componenting.traits;

import org.processmining.estminer.specpp.componenting.system.ComponentCollection;

public interface UsesLocalComponentSystem extends HasComponentCollection {

    static void bridgeTheGap(Object left, Object right) {
        bridgeTheGap(left, right, true);
    }

    static void bridgeTheGap(Object left, Object right, boolean absorbIntoLeft) {
        // TODO not like this
        if (left instanceof UsesLocalComponentSystem && right instanceof UsesLocalComponentSystem) {
            ComponentCollection leftlcr = ((UsesLocalComponentSystem) left).localComponentSystem();
            ComponentCollection rightlcr = ((UsesLocalComponentSystem) right).localComponentSystem();
            leftlcr.fulfil(rightlcr);
            if (absorbIntoLeft) leftlcr.absorb(rightlcr);
        }
        if (right instanceof UsesLocalComponentSystem) ((UsesLocalComponentSystem) right).bridgeToChildren();
        if (left instanceof UsesLocalComponentSystem && right instanceof UsesLocalComponentSystem) {
            ComponentCollection leftlcr = ((UsesLocalComponentSystem) left).localComponentSystem();
            ComponentCollection rightlcr = ((UsesLocalComponentSystem) right).localComponentSystem();
            leftlcr.fulfilFrom(rightlcr);
        }
    }

    ComponentCollection localComponentSystem();

    @Override
    default ComponentCollection getComponentCollection() {
        return localComponentSystem();
    }

    default void bridgeToChildren() {

    }

}
