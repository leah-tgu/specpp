package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.*;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.componenting.traits.UsesComponentSystem;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.supervision.piping.Observable;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.traits.Triggerable;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

public abstract class AbstractPostponingComposer<C extends Candidate, I extends Composition<C>, R extends Result, L extends CandidateConstraint<C>> implements ConstrainingComposer<C, I, R, L>, Triggerable, UsesComponentSystem {

    protected final Composer<C, I, R> childComposer;
    private final ComponentSystemAdapter componentSystemAdapter = new ComponentSystemAdapter();
    private final EventSupervision<L> constraintOutput = PipeWorks.eventSupervision();

    protected AbstractPostponingComposer(Composer<C, I, R> childComposer) {
        this.childComposer = childComposer;
    }

    protected void publishConstraint(L constraint){constraintOutput.publish(constraint);}

    @Override
    public Observable<L> getConstraintPublisher() {return constraintOutput;}

    @Override
    public void accept(C candidate) {
        if (deliberateImmediateRejection(candidate)) rejectCandidate(candidate);
        else if (deliberateImmediateAcceptance(candidate)) acceptCandidate(candidate);
        else postponeDecision(candidate);
    }

    protected abstract boolean deliberateImmediateAcceptance(C candidate);

    protected abstract boolean deliberateImmediateRejection(C candidate);

    protected abstract void postponeDecision(C candidate);

    protected abstract void handlePostponedDecisions();

    protected void acceptCandidate(C candidate) {
        childComposer.accept(candidate);
    }

    protected abstract void rejectCandidate(C candidate);

    @Override
    public boolean isFinished() {
        return childComposer.isFinished();
    }

    @Override
    public I getIntermediateResult() {
        return childComposer.getIntermediateResult();
    }

    @Override
    public R generateResult() {
        return childComposer.generateResult();
    }

    @Override
    public void trigger() {
        handlePostponedDecisions();
    }


    @Override
    public ComponentSystemAdapter componentSystemAdapter() {
        return componentSystemAdapter;
    }
}
