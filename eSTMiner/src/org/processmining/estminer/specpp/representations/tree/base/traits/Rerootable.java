package org.processmining.estminer.specpp.representations.tree.base.traits;

import org.processmining.estminer.specpp.representations.tree.base.TreeNode;

public interface Rerootable<N extends TreeNode> {

    void setRoot(N newRoot);

}
