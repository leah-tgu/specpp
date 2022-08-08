package org.processmining.estminer.specpp.datastructures.tree.base.impls;

import org.processmining.estminer.specpp.datastructures.tree.base.LocalNodeGenerator;
import org.processmining.estminer.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.estminer.specpp.datastructures.tree.base.NodeState;

import java.util.Optional;

public abstract class GeneratingLocalNode<P extends NodeProperties, S extends NodeState, N extends GeneratingLocalNode<P, S, N>> extends AbstractLocalNode<P, S, N> {
    private final LocalNodeGenerator<P, S, N> generator;

    public GeneratingLocalNode(boolean isRoot, P nodeProperties, S nodeState, LocalNodeGenerator<P, S, N> generator, int depth) {
        super(isRoot, nodeProperties, nodeState, depth);
        this.generator = generator;
    }

    public LocalNodeGenerator<P, S, N> getGenerator() {
        return generator;
    }

    protected void updateState(S newState) {
        setState(newState);
    }


    @Override
    public final boolean canExpand() {
        return canExpandBasedOnState().orElseGet(this::canExpandBasedOnGenerator);
    }

    protected abstract Optional<Boolean> canExpandBasedOnState();

    protected abstract boolean canExpandBasedOnGenerator();


}
