package org.processmining.estminer.specpp.componenting.evaluation;

import org.processmining.estminer.specpp.base.Evaluable;
import org.processmining.estminer.specpp.base.Evaluation;
import org.processmining.estminer.specpp.base.Evaluator;
import org.processmining.estminer.specpp.componenting.system.AbstractFulfilledRequirement;
import org.processmining.estminer.specpp.componenting.system.ComponentType;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

public class FulfilledEvaluatorRequirement<I extends Evaluable, E extends Evaluation> extends AbstractFulfilledRequirement<Evaluator<I, E>, EvaluatorRequirement<?, ?>> {

    public FulfilledEvaluatorRequirement(EvaluatorRequirement<?, ?> requirement, Evaluator<I, E> delegate) {
        super(requirement, JavaTypingUtils.castClass(Evaluator.class), delegate);
    }

    @Override
    public ComponentType componentType() {
        return requirement.componentType();
    }


}
