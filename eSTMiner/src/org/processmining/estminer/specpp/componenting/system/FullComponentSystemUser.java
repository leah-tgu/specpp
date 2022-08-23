package org.processmining.estminer.specpp.componenting.system;

import org.processmining.estminer.specpp.componenting.traits.UsesGlobalComponentSystem;
import org.processmining.estminer.specpp.componenting.traits.UsesLocalComponentSystem;
import org.processmining.estminer.specpp.traits.Initializable;

import java.util.stream.Stream;

public interface FullComponentSystemUser extends UsesLocalComponentSystem, UsesGlobalComponentSystem, Initializable {

    void registerSubComponent(FullComponentSystemUser subComponent);

    Stream<FullComponentSystemUser> collectTransitiveSubcomponents();

    default void connectLocalComponentSystem(LocalComponentRepository lcr) {
        collectTransitiveSubcomponents().forEachOrdered(csu -> lcr.consumeEntirely(csu.localComponentSystem()));
        lcr.fulfil(lcr);
    }

    @Override
    default ComponentCollection getComponentCollection() {
        return UsesGlobalComponentSystem.super.getComponentCollection();
    }
}
