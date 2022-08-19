package org.processmining.estminer.specpp.componenting.traits;

import org.processmining.estminer.specpp.componenting.data.DataSourceCollection;
import org.processmining.estminer.specpp.componenting.system.ComponentType;

public interface ProvidesDataSources extends HasComponentCollection {

    default DataSourceCollection dataSources() {
        return (DataSourceCollection) (getComponentCollection().getProvisions(ComponentType.Data));
    }

}
