package org.processmining.estminer.specpp.componenting.traits;

import org.processmining.estminer.specpp.componenting.data.DataRequirement;
import org.processmining.estminer.specpp.componenting.data.DataSourceCollection;
import org.processmining.estminer.specpp.componenting.system.ComponentType;

public interface ProvidesParameters extends UsesComponentSystem {

    default DataSourceCollection parameters() {
        return (DataSourceCollection) componentSystemAdapter().<DataRequirement<?>>getProvisions(ComponentType.Parameters);
    }

}
