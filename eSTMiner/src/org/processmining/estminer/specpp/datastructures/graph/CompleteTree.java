package org.processmining.estminer.specpp.datastructures.graph;

import org.processmining.estminer.specpp.datastructures.tree.base.Tree;
import org.processmining.estminer.specpp.datastructures.tree.base.TreeEdge;
import org.processmining.estminer.specpp.datastructures.tree.base.UniDiTreeNode;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.TreeTraversable;

public interface CompleteTree<N extends UniDiTreeNode<N>, E extends TreeEdge<N>> extends DirectedGraph<N, E>, Tree<N>, TreeTraversable<N, E> {

    String limitedToString(int fromLevel, int toLevel, long nodeLimit);

}
