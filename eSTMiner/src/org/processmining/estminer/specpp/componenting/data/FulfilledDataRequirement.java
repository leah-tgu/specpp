package org.processmining.estminer.specpp.componenting.data;

import org.processmining.estminer.specpp.componenting.system.AbstractFulfilledRequirement;
import org.processmining.estminer.specpp.componenting.system.ComponentType;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

public class FulfilledDataRequirement<T> extends AbstractFulfilledRequirement<DataSource<T>, DataRequirement<?>> {

    public FulfilledDataRequirement(DataRequirement<?> requirement, DataSource<T> delegate) {
        super(requirement, JavaTypingUtils.castClass(delegate.getClass()), delegate);
    }


    @Override
    public ComponentType componentType() {
        return requirement.componentType();
    }


}
