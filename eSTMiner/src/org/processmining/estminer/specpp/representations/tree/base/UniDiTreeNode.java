package org.processmining.estminer.specpp.representations.tree.base;

import org.processmining.estminer.specpp.representations.tree.base.traits.KnowsChildren;

public interface UniDiTreeNode<N extends UniDiTreeNode<N>> extends TreeNode, KnowsChildren<N> {

}
