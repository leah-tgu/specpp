package org.processmining.estminer.specpp.datastructures.tree.events;

import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;

public class NodeContractionEvent<N extends TreeNode> extends TreeNodeEvent<N> {
    public NodeContractionEvent(N source) {
        super(source);
    }

    @Override
    public String toString() {
        return "NodeContractionEvent(" + source + ")";
    }

}
