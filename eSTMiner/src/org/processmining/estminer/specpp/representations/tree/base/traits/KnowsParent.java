package org.processmining.estminer.specpp.representations.tree.base.traits;

import org.processmining.estminer.specpp.representations.tree.base.TreeNode;

public interface KnowsParent<N extends TreeNode & KnowsParent<N>> {

    N getParent();

}
