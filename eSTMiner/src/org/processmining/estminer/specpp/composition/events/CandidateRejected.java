package org.processmining.estminer.specpp.composition.events;

import org.processmining.estminer.specpp.base.Candidate;

public class CandidateRejected<C extends Candidate> extends CandidateCompositionEvent<C> {
    public CandidateRejected(C candidate) {
        super(candidate, CompositionAction.Reject);
    }
}
