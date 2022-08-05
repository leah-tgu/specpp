package org.processmining.estminer.specpp.representations.tree.base;

import org.processmining.estminer.specpp.representations.graph.Annotatable;

public interface AnnotatableBiDiNode<A, N extends AnnotatableBiDiNode<A, N>> extends BiDiTreeNode<N>, Annotatable<A> {
}
