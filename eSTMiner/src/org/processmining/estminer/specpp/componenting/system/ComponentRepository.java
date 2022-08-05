package org.processmining.estminer.specpp.componenting.system;

import org.processmining.estminer.specpp.componenting.traits.*;
import org.processmining.estminer.specpp.config.Configuration;

public class ComponentRepository extends Configuration implements UsesComponentSystem, ProvidesDataSources, ProvidesEvaluators, ProvidesSupervisors, ProvidesParameters {

    public ComponentRepository() {
        super(new ComponentSystemAdapter());
        componentSystemAdapter().addComponent(ComponentType.Data);
        componentSystemAdapter().addComponent(ComponentType.Evaluation);
        componentSystemAdapter().addComponent(ComponentType.Supervision);
        componentSystemAdapter().addComponent(ComponentType.Parameters);
    }

}
