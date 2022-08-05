package org.processmining.estminer.specpp.componenting.traits;

import org.processmining.estminer.specpp.componenting.delegators.Container;
import org.processmining.estminer.specpp.componenting.system.ComponentType;
import org.processmining.estminer.specpp.componenting.system.FulfilledRequirementsCollection;
import org.processmining.estminer.specpp.componenting.system.Requirement;

import java.util.Map;

public interface ProvisionsComponents {

    Map<ComponentType, FulfilledRequirementsCollection<?>> componentProvisions();

    public <C, R extends Requirement<? extends C, R>> void fulfil(R requirement, Container<C> container);

}
