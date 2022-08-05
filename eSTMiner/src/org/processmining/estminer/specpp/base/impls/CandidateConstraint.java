package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.ConstraintEvent;

public abstract class CandidateConstraint<C extends Candidate> implements ConstraintEvent {

    private final C affectedCandidate;

    protected CandidateConstraint(C affectedCandidate) {
        this.affectedCandidate = affectedCandidate;
    }

    public C getAffectedCandidate() {
        return affectedCandidate;
    }
}
