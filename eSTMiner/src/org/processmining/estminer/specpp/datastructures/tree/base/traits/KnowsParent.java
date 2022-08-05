package org.processmining.estminer.specpp.datastructures.tree.base.traits;

import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;

public interface KnowsParent<N extends TreeNode & KnowsParent<N>> {

    N getParent();

}
