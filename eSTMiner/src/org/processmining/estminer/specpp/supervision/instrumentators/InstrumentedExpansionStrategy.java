package org.processmining.estminer.specpp.supervision.instrumentators;

import org.processmining.estminer.specpp.base.Evaluable;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.datastructures.tree.base.ExpansionStrategy;
import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;

public class InstrumentedExpansionStrategy<N extends TreeNode & Evaluable & LocallyExpandable<N>> extends AbstractInstrumentingDelegator<ExpansionStrategy<N>> implements ExpansionStrategy<N> {

    public static final TaskDescription TREE_EXPANSION_SELECTION = new TaskDescription("Tree Expansion Selection");

    public InstrumentedExpansionStrategy(ExpansionStrategy<N> delegate) {
        super(delegate);
        componentSystemAdapter().provide(SupervisionRequirements.observable("tree.strategy.performance", PerformanceEvent.class, timeStopper));
    }

    public N nextExpansion() {
        timeStopper.start(TREE_EXPANSION_SELECTION);
        N n = delegate.nextExpansion();
        timeStopper.stop(TREE_EXPANSION_SELECTION);
        return n;
    }

    public boolean hasNextExpansion() {
        return delegate.hasNextExpansion();
    }

    public N deregisterPreviousProposal() {
        return delegate.deregisterPreviousProposal();
    }

    public void registerNode(N node) {
        delegate.registerNode(node);
    }

    public void registerPotentialNodes(Iterable<N> potentialNodes) {
        delegate.registerPotentialNodes(potentialNodes);
    }

    public void deregisterNode(N node) {
        delegate.deregisterNode(node);
    }
}
