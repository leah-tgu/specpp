package org.processmining.estminer.specpp.datastructures.tree.events;

import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;
import org.processmining.estminer.specpp.traits.RepresentsChange;

public abstract class LeafEvent<N extends TreeNode> extends TreeNodeEvent<N> implements RepresentsChange {
    protected LeafEvent(N source) {
        super(source);
    }
}
