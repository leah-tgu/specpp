package org.processmining.estminer.specpp.representations.tree.base;

import org.processmining.estminer.specpp.representations.graph.CompleteTree;
import org.processmining.estminer.specpp.representations.tree.base.traits.Rerootable;

public interface BiDiTree<N extends BiDiTreeNode<N>> extends CompleteTree<N, TreeEdge<N>>, Iterable<N>, Rerootable<N> {


}
