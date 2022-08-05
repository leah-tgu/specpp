package org.processmining.estminer.specpp.datastructures.tree.base;

import org.processmining.estminer.specpp.datastructures.tree.base.impls.AbstractLocalNode;

public interface ParentGenerator<P extends NodeProperties, S extends NodeState, N extends AbstractLocalNode<P, S, N>> {

    N generateParent(N child);

}
