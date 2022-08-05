package org.processmining.estminer.specpp.representations.tree.constraints;

import org.processmining.estminer.specpp.representations.tree.base.GenerationConstraint;

public class DepthConstraint implements GenerationConstraint {
    private final int maxDepth;

    public DepthConstraint(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getDepth() {
        return maxDepth;
    }
}
