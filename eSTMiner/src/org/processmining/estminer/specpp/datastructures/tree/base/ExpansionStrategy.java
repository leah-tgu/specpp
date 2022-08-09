package org.processmining.estminer.specpp.datastructures.tree.base;

import org.processmining.estminer.specpp.datastructures.tree.base.traits.LocallyExpandable;

public interface ExpansionStrategy<N extends TreeNode & LocallyExpandable<N>> extends TreeStrategy<N> {

    N nextExpansion();

    boolean hasNextExpansion();

    N deregisterPreviousProposal();

}
