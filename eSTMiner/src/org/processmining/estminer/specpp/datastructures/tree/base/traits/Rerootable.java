package org.processmining.estminer.specpp.datastructures.tree.base.traits;

import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;

public interface Rerootable<N extends TreeNode> {

    void setRoot(N newRoot);

}
