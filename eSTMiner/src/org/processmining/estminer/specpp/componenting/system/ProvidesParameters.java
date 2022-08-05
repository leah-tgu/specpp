package org.processmining.estminer.specpp.componenting.system;

import org.processmining.estminer.specpp.componenting.data.DataRequirement;
import org.processmining.estminer.specpp.componenting.data.DataSourceCollection;
import org.processmining.estminer.specpp.componenting.traits.UsesComponentSystem;

public interface ProvidesParameters extends UsesComponentSystem {

    default DataSourceCollection parameters() {
        return (DataSourceCollection) componentSystemAdapter().<DataRequirement<?>>getProvisions(ComponentType.Parameters);
    }

}
