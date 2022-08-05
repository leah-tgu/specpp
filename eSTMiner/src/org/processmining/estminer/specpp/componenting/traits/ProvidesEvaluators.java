package org.processmining.estminer.specpp.componenting.traits;

import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorCollection;
import org.processmining.estminer.specpp.componenting.system.ComponentType;

public interface ProvidesEvaluators extends UsesComponentSystem {

    default EvaluatorCollection evaluators() {
        return ((EvaluatorCollection) componentSystemAdapter().getProvisions(ComponentType.Evaluation));
    }

}
