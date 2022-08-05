package org.processmining.estminer.specpp.representations.tree.base.impls;

import org.processmining.estminer.specpp.representations.tree.base.ExpansionStrategy;
import org.processmining.estminer.specpp.representations.tree.base.TreeNode;
import org.processmining.estminer.specpp.representations.tree.base.traits.LocallyExpandable;

import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Supplier;

public class VariableExpansion<N extends TreeNode & LocallyExpandable<N>> implements ExpansionStrategy<N> {

    private final Deque<N> buffer;
    private final Supplier<N> peeker, dequeuer;

    public VariableExpansion() {
        this(true);
    }

    public VariableExpansion(boolean useStack) {
        buffer = new LinkedList<>();
        peeker = useStack ? buffer::peekLast : buffer::peekFirst;
        dequeuer = useStack ? buffer::removeLast : buffer::removeFirst;
    }

    public static <N extends TreeNode & LocallyExpandable<N>> VariableExpansion<N> dfs() {
        return new VariableExpansion<N>(true);
    }

    public static <N extends TreeNode & LocallyExpandable<N>> VariableExpansion<N> bfs() {
        return new VariableExpansion<N>(false);
    }

    @Override
    public N nextExpansion() {
        return peeker.get();
    }

    @Override
    public boolean hasNextExpansion() {
        return !buffer.isEmpty();
    }

    @Override
    public void deregisterPreviousProposal() {
        dequeuer.get();
    }

    @Override
    public void registerNode(N node) {
        buffer.addLast(node);
    }

    @Override
    public void registerPotentialNodes(Iterable<N> potentialNodes) {

    }

    @Override
    public void deregisterNode(N node) {
        buffer.removeIf(n -> n.equals(node));
    }

}
