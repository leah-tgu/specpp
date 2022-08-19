package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.Composer;
import org.processmining.estminer.specpp.base.Composition;
import org.processmining.estminer.specpp.base.Result;

public abstract class RecursiveComposer<C extends Candidate, I extends Composition<C>, R extends Result> implements Composer<C, I, R> {

    protected final Composer<C, I, R> childComposer;

    public RecursiveComposer(Composer<C, I, R> childComposer) {
        this.childComposer = childComposer;
    }

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

    protected void forward(C c) {
        childComposer.accept(c);
    }

    @Override
    public abstract void accept(C c);
}

