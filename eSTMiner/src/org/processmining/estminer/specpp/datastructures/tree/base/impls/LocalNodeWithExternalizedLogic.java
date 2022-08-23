package org.processmining.estminer.specpp.datastructures.tree.base.impls;

import org.processmining.estminer.specpp.datastructures.tree.base.ChildGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.estminer.specpp.datastructures.tree.base.NodeState;

import java.util.Optional;

public abstract class LocalNodeWithExternalizedLogic<P extends NodeProperties, S extends NodeState, N extends LocalNodeWithExternalizedLogic<P, S, N>> extends AbstractLocalNode<P, S, N> {
    private final ChildGenerationLogic<P, S, N> generationLogic;

    public LocalNodeWithExternalizedLogic(boolean isRoot, P nodeProperties, S nodeState, ChildGenerationLogic<P, S, N> generationLogic, int depth) {
        super(isRoot, nodeProperties, nodeState, depth);
        this.generationLogic = generationLogic;
    }

    public ChildGenerationLogic<P, S, N> getGenerationLogic() {
        return generationLogic;
    }

    @Override
    public final boolean canExpand() {
        return canExpandBasedOnState().orElseGet(this::canExpandBasedOnGenerator);
    }

    protected abstract Optional<Boolean> canExpandBasedOnState();

    protected abstract boolean canExpandBasedOnGenerator();


}
