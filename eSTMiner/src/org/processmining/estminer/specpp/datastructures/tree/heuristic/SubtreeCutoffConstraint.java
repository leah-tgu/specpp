package org.processmining.estminer.specpp.datastructures.tree.heuristic;

import org.processmining.estminer.specpp.datastructures.tree.base.LocalNode;

public class SubtreeCutoffConstraint<N extends LocalNode<?, ?, N>> extends LocalNodeGenerationConstraint<N> {
    protected SubtreeCutoffConstraint(N affectedNode) {
        super(affectedNode);
    }
}
