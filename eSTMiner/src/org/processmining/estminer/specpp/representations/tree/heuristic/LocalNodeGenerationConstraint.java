package org.processmining.estminer.specpp.representations.tree.heuristic;

import org.processmining.estminer.specpp.representations.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.representations.tree.base.LocalNode;

public abstract class LocalNodeGenerationConstraint<N extends LocalNode<?, ?, N>> implements GenerationConstraint {

    private final N affectedNode;

    public N getAffectedNode() {
        return affectedNode;
    }

    protected LocalNodeGenerationConstraint(N affectedNode) {
        this.affectedNode = affectedNode;
    }

}
