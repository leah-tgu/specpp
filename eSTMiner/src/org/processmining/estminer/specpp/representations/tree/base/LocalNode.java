package org.processmining.estminer.specpp.representations.tree.base;

import org.processmining.estminer.specpp.base.Evaluable;
import org.processmining.estminer.specpp.representations.tree.base.traits.KnowsDepth;
import org.processmining.estminer.specpp.representations.tree.base.traits.LocallyContractible;
import org.processmining.estminer.specpp.representations.tree.base.traits.LocallyExpandable;
import org.processmining.estminer.specpp.representations.tree.base.traits.StateNode;

public interface LocalNode<P extends NodeProperties, S extends NodeState, N extends LocalNode<P, S, N>> extends PropertyNode<P>, StateNode<S>, LocallyExpandable<N>, LocallyContractible<N>, KnowsDepth, Evaluable {

    boolean isRoot();
}
