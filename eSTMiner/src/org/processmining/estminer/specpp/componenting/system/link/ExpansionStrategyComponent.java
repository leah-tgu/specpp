package org.processmining.estminer.specpp.componenting.system.link;

import org.processmining.estminer.specpp.componenting.system.FullComponentSystemUser;
import org.processmining.estminer.specpp.datastructures.tree.base.ExpansionStrategy;
import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.LocallyExpandable;

public interface ExpansionStrategyComponent<N extends TreeNode & LocallyExpandable<N>> extends ExpansionStrategy<N>, FullComponentSystemUser {
}
