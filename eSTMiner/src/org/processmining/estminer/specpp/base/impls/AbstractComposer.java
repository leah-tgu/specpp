package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.Composer;
import org.processmining.estminer.specpp.base.MutableCappedComposition;
import org.processmining.estminer.specpp.base.Result;

import java.util.function.Function;

public abstract class AbstractComposer<C extends Candidate, I extends MutableCappedComposition<C>, R extends Result> implements Composer<C, I, R> {

    protected final Function<? super I, R> assembleResult;
    protected I composition;

    public AbstractComposer(I composition, Function<? super I, R> assembleResult) {
        this.composition = composition;
        this.assembleResult = assembleResult;
    }

    @Override
    public void accept(C candidate) {
        if (deliberateAcceptance(candidate)) {
            acceptCandidate(candidate);
        } else rejectCandidate(candidate);
    }

    protected I composition() {
        return composition;
    }

    protected abstract boolean deliberateAcceptance(C candidate);

    protected final void acceptCandidate(C candidate) {
        composition.accept(candidate);
        candidateAccepted(candidate);
    }

    protected final void rejectCandidate(C candidate) {
        candidateRejected(candidate);
    }

    protected final void revokeAcceptance(C candidate) {
        composition.remove(candidate);
        acceptanceRevoked(candidate);
    }

    protected final void revokeLastAcceptance() {
        C last = composition.removeLast();
        acceptanceRevoked(last);
    }

    protected abstract void acceptanceRevoked(C candidate);

    protected abstract void candidateAccepted(C candidate);

    protected abstract void candidateRejected(C candidate);

    @Override
    public boolean isFinished() {
        return !composition.hasCapacityLeft();
    }

    @Override
    public final I getIntermediateResult() {
        return composition;
    }

    @Override
    public final R generateResult() {
        return assembleResult.apply(composition);
    }


}
