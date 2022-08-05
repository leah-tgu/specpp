package org.processmining.estminer.specpp.representations.tree.base;

public interface TreeStrategy<N extends TreeNode> {

    void registerNode(N node);

    void registerPotentialNodes(Iterable<N> potentialNodes);

    void deregisterNode(N node);


}
