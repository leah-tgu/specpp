package org.processmining.estminer.specpp.representations.tree.events;

import org.processmining.estminer.specpp.representations.tree.base.TreeNode;

public class NodeRemovalEvent<N extends TreeNode> extends TreeNodeEvent<N> {
    public NodeRemovalEvent(N source) {
        super(source);
    }

    @Override
    public String toString() {
        return "NodeRemovalEvent(" + source + ")";
    }
}
