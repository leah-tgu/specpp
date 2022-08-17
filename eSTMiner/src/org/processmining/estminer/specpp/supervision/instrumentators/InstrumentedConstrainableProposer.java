package org.processmining.estminer.specpp.supervision.instrumentators;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.ConstrainableProposer;
import org.processmining.estminer.specpp.base.impls.CandidateConstraint;

public class InstrumentedConstrainableProposer<C extends Candidate, L extends CandidateConstraint<C>> extends AbstractInstrumentingDelegator<ConstrainableProposer<C, L>> implements ConstrainableProposer<C, L> {
    public InstrumentedConstrainableProposer(ConstrainableProposer<C, L> delegate) {
        super(delegate);
    }

    public C proposeCandidate() {
        return delegate.proposeCandidate();
    }

    public boolean isExhausted() {
        return delegate.isExhausted();
    }

    public void acceptConstraint(L constraint) {
        delegate.acceptConstraint(constraint);
    }

    @Override
    public Class<L> getAcceptedConstraintClass() {
        return delegate.getAcceptedConstraintClass();
    }

}
