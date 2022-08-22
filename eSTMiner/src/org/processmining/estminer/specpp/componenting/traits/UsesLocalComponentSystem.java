package org.processmining.estminer.specpp.componenting.traits;

import org.processmining.estminer.specpp.componenting.system.ComponentCollection;

public interface UsesLocalComponentSystem extends HasComponentCollection {

    static void bridgeTheGap(Object left, Object right) {
        bridgeTheGap(left, right, true);
    }

    static void bridgeTheGap(Object left, Object right, boolean absorbIntoLeft) {
        if (left instanceof UsesLocalComponentSystem && right instanceof UsesLocalComponentSystem) {
            ComponentCollection leftlcr = ((UsesLocalComponentSystem) left).localComponentSystem();
            ComponentCollection rightlcr = ((UsesLocalComponentSystem) right).localComponentSystem();
            leftlcr.fulfil(rightlcr);
            leftlcr.fulfilFrom(rightlcr);
            if (absorbIntoLeft) leftlcr.absorb(rightlcr);
        }
    }

    ComponentCollection localComponentSystem();

    @Override
    default ComponentCollection getComponentCollection() {
        return localComponentSystem();
    }
}
