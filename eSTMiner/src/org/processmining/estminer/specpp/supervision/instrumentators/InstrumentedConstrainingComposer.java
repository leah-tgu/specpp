package org.processmining.estminer.specpp.supervision.instrumentators;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.ConstrainingComposer;
import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.base.impls.CandidateConstraint;
import org.processmining.estminer.specpp.supervision.piping.Observable;

public class InstrumentedConstrainingComposer<C extends Candidate, I extends AdvancedComposition<C>, R extends Result, L extends CandidateConstraint<C>> extends AbstractInstrumentingDelegator<ConstrainingComposer<C, I, R, L>> implements ConstrainingComposer<C, I, R, L> {
    public InstrumentedConstrainingComposer(ConstrainingComposer<C, I, R, L> delegate) {
        super(delegate);
    }

    public void accept(C candidate) {
        delegate.accept(candidate);
    }

    public boolean isFinished() {
        return delegate.isFinished();
    }

    public I getIntermediateResult() {
        return delegate.getIntermediateResult();
    }

    public R generateResult() {
        return delegate.generateResult();
    }

    public Observable<L> getConstraintPublisher() {
        return delegate.getConstraintPublisher();
    }

    @Override
    public Class<L> getPublishedConstraintClass() {
        return delegate.getPublishedConstraintClass();
    }

}
