package org.processmining.estminer.specpp.datastructures.tree.events;

import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.LocallyExpandable;

public class LeafRemovalEvent<N extends TreeNode & LocallyExpandable<N>> extends LeafEvent<N> {

    public LeafRemovalEvent(N source) {
        super(source);
    }

    @Override
    public String toString() {
        return "LeafRemovedEvent(" + source + ")";
    }

    @Override
    public int getDelta() {
        return -1;
    }
}
