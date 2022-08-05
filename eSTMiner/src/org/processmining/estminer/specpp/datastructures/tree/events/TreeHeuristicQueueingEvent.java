package org.processmining.estminer.specpp.datastructures.tree.events;

import org.processmining.estminer.specpp.base.Evaluable;
import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.estminer.specpp.traits.RepresentsChange;

public abstract class TreeHeuristicQueueingEvent<N extends TreeNode & Evaluable & LocallyExpandable<N>> extends TreeNodeEvent<N> implements TreeHeuristicsEvent, RepresentsChange {
    protected TreeHeuristicQueueingEvent(N source) {
        super(source);
    }

}
