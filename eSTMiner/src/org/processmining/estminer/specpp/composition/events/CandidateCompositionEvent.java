package org.processmining.estminer.specpp.composition.events;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.supervision.observations.Event;


public class CandidateCompositionEvent<C extends Candidate> implements Event {

    protected final C candidate;
    protected final CompositionAction action;

    public CandidateCompositionEvent(C candidate, CompositionAction action) {
        this.candidate = candidate;
        this.action = action;
    }


    public C getCandidate() {
        return candidate;
    }

    public CompositionAction getAction() {
        return action;
    }
}
