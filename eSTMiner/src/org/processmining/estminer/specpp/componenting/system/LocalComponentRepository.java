package org.processmining.estminer.specpp.componenting.system;

import org.processmining.estminer.specpp.componenting.traits.*;

public class LocalComponentRepository extends ComponentCollection implements UsesLocalComponentSystem, ProvidesDataSources, ProvidesEvaluators, ProvidesSupervisors, ProvidesParameters {

    public LocalComponentRepository() {
        addComponent(ComponentType.Data);
        addComponent(ComponentType.Evaluation);
        addComponent(ComponentType.Supervision);
        addComponent(ComponentType.Parameters);
    }

    @Override
    public ComponentCollection localComponentSystem() {
        return this;
    }

}
