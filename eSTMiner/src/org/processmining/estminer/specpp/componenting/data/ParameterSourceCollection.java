package org.processmining.estminer.specpp.componenting.data;

import org.processmining.estminer.specpp.componenting.system.ComponentType;
import org.processmining.estminer.specpp.componenting.traits.IsGlobalProvider;

public class ParameterSourceCollection extends DataSourceCollection implements IsGlobalProvider {

    @Override
    public ComponentType componentType() {
        return ComponentType.Parameters;
    }
}
