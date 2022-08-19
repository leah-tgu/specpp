package org.processmining.estminer.specpp.componenting.system;

import org.processmining.estminer.specpp.config.SimpleBuilder;

public abstract class ComponentSystemAwareBuilder<T> extends AbstractGlobalComponentSystemUser implements SimpleBuilder<T> {

    protected abstract T buildIfFullySatisfied();

    @Override
    public T build() {
        if (getComponentCollection().areAllRequirementsMet())
            return buildIfFullySatisfied();
        else throw new RequirementsNotSatisfiedException(getComponentCollection().toString());
    }

}
