package org.processmining.estminer.specpp.componenting.delegators;

import org.processmining.estminer.specpp.base.Evaluable;
import org.processmining.estminer.specpp.base.Evaluation;
import org.processmining.estminer.specpp.base.Evaluator;

public class DelegatingEvaluator<I extends Evaluable, E extends Evaluation> extends AbstractDelegator<Evaluator<? super I, ? extends E>> implements Evaluator<I, E> {

    public DelegatingEvaluator() {
    }

    public DelegatingEvaluator(Evaluator<I, E> delegate) {
        super(delegate);
    }

    @Override
    public E eval(I input) {
        return delegate.eval(input);
    }

}
