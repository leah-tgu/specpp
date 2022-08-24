package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.componenting.system.link.ChildGenerationLogicComponent;
import org.processmining.estminer.specpp.componenting.system.link.EfficientTreeComponent;
import org.processmining.estminer.specpp.datastructures.tree.base.ChildGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.LocalNodeWithExternalizedLogic;

public abstract class EfficientTreeWithExternalizedLogicBasedProposer<C extends Candidate & NodeProperties, N extends LocalNodeWithExternalizedLogic<C, ?, N>> extends AbstractEfficientTreeBasedProposer<C, N> {

    protected final ChildGenerationLogic<C, ?, N> generationLogic;

    public EfficientTreeWithExternalizedLogicBasedProposer(ChildGenerationLogicComponent<C, ?, N> generationLogic, EfficientTreeComponent<N> tree) {
        super(tree);
        this.generationLogic = generationLogic;
        registerSubComponent(generationLogic);
    }

    public ChildGenerationLogic<C, ?, N> getGenerationLogic() {
        return generationLogic;
    }
}
