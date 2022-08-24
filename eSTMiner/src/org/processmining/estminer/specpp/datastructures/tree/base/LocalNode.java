package org.processmining.estminer.specpp.datastructures.tree.base;

import org.processmining.estminer.specpp.base.Evaluable;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.KnowsDepth;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.StateNode;

public interface LocalNode<P extends NodeProperties, S extends NodeState, N extends LocalNode<P, S, N>> extends PropertyNode<P>, StateNode<S>, LocallyExpandable<N>, KnowsDepth, Evaluable {

    boolean isRoot();
}
