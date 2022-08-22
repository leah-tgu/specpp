package org.processmining.estminer.specpp.supervision.instrumentators;

import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.datastructures.tree.base.EfficientTree;
import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;

import java.util.Collection;

public class InstrumentedEfficientTree<N extends TreeNode & LocallyExpandable<N>> extends AbstractInstrumentingDelegator<EfficientTree<N>> implements EfficientTree<N> {

    public static final TaskDescription TREE_EXPANSION = new TaskDescription("Tree Expansion");

    public InstrumentedEfficientTree(EfficientTree<N> delegate) {
        super(delegate);
        componentSystemAdapter().provide(SupervisionRequirements.observable("tree.performance", PerformanceEvent.class, timeStopper));
    }

    public N getRoot() {
        return delegate.getRoot();
    }

    public Collection<N> getLeaves() {
        return delegate.getLeaves();
    }

    public N tryExpandingTree() {
        timeStopper.start(TREE_EXPANSION);
        N n = delegate.tryExpandingTree();
        timeStopper.stop(TREE_EXPANSION);
        return n;
    }

    public void setRootOnce(N root) {
        delegate.setRootOnce(root);
    }

}
