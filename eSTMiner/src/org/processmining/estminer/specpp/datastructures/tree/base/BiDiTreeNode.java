package org.processmining.estminer.specpp.datastructures.tree.base;

import org.processmining.estminer.specpp.datastructures.tree.base.traits.KnowsParent;

public interface BiDiTreeNode<N extends BiDiTreeNode<N>> extends UniDiTreeNode<N>, KnowsParent<N> {

}
