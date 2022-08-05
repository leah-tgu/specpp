package org.processmining.estminer.specpp.componenting.data;

import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.system.ComponentType;
import org.processmining.estminer.specpp.config.parameters.Parameters;

public class ParameterRequirement<P extends Parameters> extends DataRequirement<P> {
    public ParameterRequirement(String label, Class<P> dataType) {
        super(label, dataType);
    }

    @Override
    public String toString() {
        return "ParameterRequirement(\"" + label + "\", " + dataType.getSimpleName() + ")";
    }

    @Override
    public ComponentType componentType() {
        return ComponentType.Parameters;
    }

    @Override
    public DelegatingDataSource<P> defaultingDelegator(P defaultData) {
        return super.defaultingDelegator(defaultData);
    }
}
