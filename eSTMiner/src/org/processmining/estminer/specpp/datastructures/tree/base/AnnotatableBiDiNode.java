package org.processmining.estminer.specpp.datastructures.tree.base;

import org.processmining.estminer.specpp.datastructures.graph.Annotatable;

public interface AnnotatableBiDiNode<A, N extends AnnotatableBiDiNode<A, N>> extends BiDiTreeNode<N>, Annotatable<A> {
}
