package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.componenting.system.FullComponentSystemUser;
import org.processmining.estminer.specpp.datastructures.tree.base.ChildGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.base.EfficientTree;
import org.processmining.estminer.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.LocalNodeWithExternalizedLogic;

public abstract class EfficientTreeWithExternalizedLogicBasedProposer<C extends Candidate & NodeProperties, N extends LocalNodeWithExternalizedLogic<C, ?, N>, G extends ChildGenerationLogic<C, ?, N>> extends AbstractEfficientTreeBasedProposer<C, N> {

    protected final G generationLogic;

    public EfficientTreeWithExternalizedLogicBasedProposer(G generationLogic, EfficientTree<N> tree) {
        super(tree);
        this.generationLogic = generationLogic;
        if (generationLogic instanceof FullComponentSystemUser) registerSubComponent(((FullComponentSystemUser) generationLogic));
    }

}
