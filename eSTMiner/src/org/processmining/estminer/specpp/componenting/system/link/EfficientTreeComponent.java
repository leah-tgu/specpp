package org.processmining.estminer.specpp.componenting.system.link;

import org.processmining.estminer.specpp.componenting.system.FullComponentSystemUser;
import org.processmining.estminer.specpp.datastructures.tree.base.EfficientTree;
import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.LocallyExpandable;

public interface EfficientTreeComponent<N extends TreeNode & LocallyExpandable<N>> extends EfficientTree<N>, FullComponentSystemUser {
}
