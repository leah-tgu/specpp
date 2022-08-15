package org.processmining.estminer.specpp.composition.events;

import org.processmining.estminer.specpp.base.Candidate;

public class CandidateAccepted<C extends Candidate> extends CandidateCompositionEvent<C> {
    public CandidateAccepted(C candidate) {
        super(candidate, CompositionAction.Accept);
    }
}
