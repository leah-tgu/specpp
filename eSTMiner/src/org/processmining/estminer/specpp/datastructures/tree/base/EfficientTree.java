package org.processmining.estminer.specpp.datastructures.tree.base;

import org.processmining.estminer.specpp.datastructures.tree.base.traits.DelayedRooting;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.LocallyExpandable;

import java.util.Iterator;

public interface EfficientTree<N extends TreeNode & LocallyExpandable<N>> extends Tree<N>, DelayedRooting<N> {

    Iterator<N> getLeaves();

    N expandTree();

}
