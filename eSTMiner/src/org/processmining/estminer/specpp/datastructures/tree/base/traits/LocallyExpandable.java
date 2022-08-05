package org.processmining.estminer.specpp.datastructures.tree.base.traits;

import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;

public interface LocallyExpandable<N extends TreeNode & LocallyExpandable<N>> {

    Iterable<N> generatePotentialChildren();

    boolean didExpand();

    boolean canExpand();

    N generateChild();

}
