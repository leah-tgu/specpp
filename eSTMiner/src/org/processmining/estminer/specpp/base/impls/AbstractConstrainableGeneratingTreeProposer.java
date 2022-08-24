package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.ConstrainableProposer;
import org.processmining.estminer.specpp.base.Constrainer;
import org.processmining.estminer.specpp.componenting.system.link.AbstractBaseClass;
import org.processmining.estminer.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.LocalNodeWithExternalizedLogic;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;

public abstract class AbstractConstrainableGeneratingTreeProposer<C extends Candidate & NodeProperties, N extends LocalNodeWithExternalizedLogic<C, ?, N>, L extends CandidateConstraint<C>, K extends GenerationConstraint> extends AbstractBaseClass implements ConstrainableProposer<C, L>, Constrainer<K> {

    protected final EventSupervision<K> constraintOutput = PipeWorks.eventSupervision();

    protected final EfficientTreeWithExternalizedLogicBasedProposer<C, N> delegate;

    public AbstractConstrainableGeneratingTreeProposer(EfficientTreeWithExternalizedLogicBasedProposer<C, N> delegate) {
        this.delegate = delegate;
        registerSubComponent(delegate);
    }


}
