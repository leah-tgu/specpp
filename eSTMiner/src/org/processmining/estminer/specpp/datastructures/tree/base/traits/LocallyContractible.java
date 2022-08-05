package org.processmining.estminer.specpp.datastructures.tree.base.traits;

import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;

public interface LocallyContractible<N extends TreeNode & LocallyContractible<N>> {

    N generateParent();

    boolean canContract();

}
