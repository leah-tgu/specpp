package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.Composer;
import org.processmining.estminer.specpp.base.Composition;
import org.processmining.estminer.specpp.base.Result;

public abstract class FilteringComposer<C extends Candidate, I extends Composition<C>, R extends Result> extends RecursiveComposer<C, I, R> implements Composer<C, I, R> {
    public FilteringComposer(Composer<C, I, R> childComposer) {
        super(childComposer);
    }

    @Override
    public abstract void accept(C c);

}
