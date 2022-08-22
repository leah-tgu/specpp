package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.*;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.componenting.traits.UsesGlobalComponentSystem;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.supervision.piping.Observable;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.traits.Triggerable;

public abstract class AbstractPostponingComposer<C extends Candidate, I extends Composition<C>, R extends Result, L extends CandidateConstraint<C>> extends RecursiveComposer<C, I, R> implements ConstrainingComposer<C, I, R, L>, Triggerable, UsesGlobalComponentSystem {

    private final GlobalComponentRepository gcr = new GlobalComponentRepository();
    private final EventSupervision<L> constraintOutput = PipeWorks.eventSupervision();

    public AbstractPostponingComposer(Composer<C, I, R> childComposer) {
        super(childComposer);
    }

    protected final void publishConstraint(L constraint) {
        constraintOutput.publish(constraint);
    }

    @Override
    public final Observable<L> getConstraintPublisher() {
        return constraintOutput;
    }

    @Override
    public ComponentCollection getComponentCollection() {
        return gcr;
    }

    @Override
    public void accept(C candidate) {
        CandidateDecision candidateDecision = deliberateCandidate(candidate);
        switch (candidateDecision) {
            case Accept:
                acceptCandidate(candidate);
                break;
            case Reject:
                rejectCandidate(candidate);
                break;
            case Discard:
                discardCandidate(candidate);
                break;
            case Postpone:
                postponeDecision(candidate);
                break;
        }
    }


    public enum CandidateDecision {
        Accept, Reject, Discard, Postpone;
    }

    protected abstract CandidateDecision deliberateCandidate(C candidate);

    protected abstract CandidateDecision reDeliberateCandidate(C candidate);


    protected abstract boolean handlePostponedDecisions();

    protected void handlePostponedDecisionsUntilNoChange() {
        int limit = 100;
        int count = 0;
        while (count++ < limit && handlePostponedDecisions()) ;
    }

    protected abstract void postponeDecision(C candidate);

    protected void acceptCandidate(C candidate) {
        forward(candidate);
    }

    protected abstract void rejectCandidate(C candidate);

    protected abstract void discardCandidate(C candidate);

    @Override
    public final void trigger() {
        handlePostponedDecisionsUntilNoChange();
    }


    @Override
    public final ComponentCollection componentSystemAdapter() {
        return gcr;
    }
}
