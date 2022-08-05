package org.processmining.estminer.specpp.representations.tree.events;

import org.processmining.estminer.specpp.representations.tree.base.TreeNode;

public class NodeExhaustionEvent<N extends TreeNode> extends TreeNodeEvent<N> {

    public NodeExhaustionEvent(N source) {
        super(source);
    }

    @Override
    public String toString() {
        return "NodeExhaustionEvent(" + source + ")";
    }

}
