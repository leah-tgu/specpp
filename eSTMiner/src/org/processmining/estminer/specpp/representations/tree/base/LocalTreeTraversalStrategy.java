package org.processmining.estminer.specpp.representations.tree.base;

import org.processmining.estminer.specpp.representations.tree.base.traits.LocallyContractible;
import org.processmining.estminer.specpp.representations.tree.base.traits.LocallyExpandable;

public interface LocalTreeTraversalStrategy<N extends TreeNode & LocallyExpandable<N> & LocallyContractible<N>> extends ExpansionStrategy<N>, ContractionStrategy<N> {

}
