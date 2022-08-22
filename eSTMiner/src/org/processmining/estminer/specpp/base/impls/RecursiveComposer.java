package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.Composer;
import org.processmining.estminer.specpp.base.Composition;
import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.system.LocalComponentRepository;
import org.processmining.estminer.specpp.componenting.traits.UsesLocalComponentSystem;
import org.processmining.estminer.specpp.traits.Initializable;

public abstract class RecursiveComposer<C extends Candidate, I extends Composition<C>, R extends Result> implements Composer<C, I, R>, Initializable, UsesLocalComponentSystem {

    protected LocalComponentRepository lcr = new LocalComponentRepository();
    protected final Composer<C, I, R> childComposer;

    public RecursiveComposer(Composer<C, I, R> childComposer) {
        this.childComposer = childComposer;
    }

    @Override
    public void init() {
        UsesLocalComponentSystem.bridgeTheGap(this, childComposer);
        if (childComposer instanceof Initializable) ((Initializable) childComposer).init();
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
    public ComponentCollection localComponentSystem() {
        return lcr;
    }

    @Override
    public abstract void accept(C c);
}

