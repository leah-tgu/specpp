package org.processmining.estminer.specpp.componenting.traits;

import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirement;
import org.processmining.estminer.specpp.componenting.system.ComponentType;
import org.processmining.estminer.specpp.componenting.system.FulfilledRequirementsCollection;

public interface ProvidesSupervisors extends UsesComponentSystem {

    default FulfilledRequirementsCollection<SupervisionRequirement> supervisors() {
        return componentSystemAdapter().getProvisions(ComponentType.Supervision);
    }

}
