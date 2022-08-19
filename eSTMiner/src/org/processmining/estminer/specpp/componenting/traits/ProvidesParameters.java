package org.processmining.estminer.specpp.componenting.traits;

import org.processmining.estminer.specpp.componenting.data.DataRequirement;
import org.processmining.estminer.specpp.componenting.data.DataSourceCollection;
import org.processmining.estminer.specpp.componenting.system.ComponentType;

public interface ProvidesParameters extends HasComponentCollection {

    default DataSourceCollection parameters() {
        return (DataSourceCollection) getComponentCollection().<DataRequirement<?>>getProvisions(ComponentType.Parameters);
    }

}
