package org.processmining.estminer.specpp.representations.tree.events;

import org.processmining.estminer.specpp.representations.tree.base.TreeNode;
import org.processmining.estminer.specpp.traits.RepresentsChange;

public abstract class LeafEvent<N extends TreeNode> extends TreeNodeEvent<N> implements RepresentsChange {
    protected LeafEvent(N source) {
        super(source);
    }
}
