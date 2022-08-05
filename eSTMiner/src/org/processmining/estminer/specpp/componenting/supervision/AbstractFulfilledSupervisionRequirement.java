package org.processmining.estminer.specpp.componenting.supervision;

import org.processmining.estminer.specpp.componenting.system.AbstractFulfilledRequirement;
import org.processmining.estminer.specpp.componenting.system.ComponentType;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

public abstract class AbstractFulfilledSupervisionRequirement<D> extends AbstractFulfilledRequirement<D, SupervisionRequirement> {

    public AbstractFulfilledSupervisionRequirement(SupervisionRequirement requirement, D delegate) {
        super(requirement, JavaTypingUtils.castClass(delegate.getClass()), delegate);
    }

    @Override
    public ComponentType componentType() {
        return requirement.componentType();
    }

}
