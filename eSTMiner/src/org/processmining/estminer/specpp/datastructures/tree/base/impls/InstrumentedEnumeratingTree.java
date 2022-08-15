package org.processmining.estminer.specpp.datastructures.tree.base.impls;

import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.componenting.traits.UsesComponentSystem;
import org.processmining.estminer.specpp.datastructures.tree.base.ExpansionStrategy;
import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.estminer.specpp.datastructures.tree.events.LeafAdditionEvent;
import org.processmining.estminer.specpp.datastructures.tree.events.LeafRemovalEvent;
import org.processmining.estminer.specpp.datastructures.tree.events.NodeExhaustionEvent;
import org.processmining.estminer.specpp.datastructures.tree.events.NodeExpansionEvent;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.datastructures.tree.events.EnumeratingTreeStatsEvent;
import org.processmining.estminer.specpp.datastructures.tree.events.TreeEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.supervision.piping.AsyncAdHocObservableWrapper;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;

public class InstrumentedEnumeratingTree<N extends TreeNode & LocallyExpandable<N>> extends EnumeratingTree<N> implements UsesComponentSystem {


    private final ComponentSystemAdapter componentSystemAdapter = new ComponentSystemAdapter();

    private final EventSupervision<TreeEvent> eventSupervision = PipeWorks.eventSupervision();

    protected final TimeStopper timeStopper = new TimeStopper();

    public InstrumentedEnumeratingTree(N root, ExpansionStrategy<N> expansionStrategy) {
        super(root, expansionStrategy);
        makeProvisions();
    }

    public InstrumentedEnumeratingTree(ExpansionStrategy<N> expansionStrategy) {
        super(expansionStrategy);
        makeProvisions();
    }

    protected void makeProvisions() {
        componentSystemAdapter.provide(SupervisionRequirements.observable("tree.events", TreeEvent.class, eventSupervision))
                              .provide(SupervisionRequirements.adHocObservable("tree.stats", EnumeratingTreeStatsEvent.class, AsyncAdHocObservableWrapper.wrap(() -> new EnumeratingTreeStatsEvent(leaves.size()))))
                              .provide(SupervisionRequirements.observable("tree.performance", PerformanceEvent.class, timeStopper));

    }

    @Override
    protected N expand() {
        timeStopper.start(TaskDescription.TREE_EXPANSION);
        N child = super.expand();
        timeStopper.stop(TaskDescription.TREE_EXPANSION);
        return child;
    }

    @Override
    protected void nodeExpanded(N node, N child) {
        eventSupervision.observe(new NodeExpansionEvent<>(node, child));
        super.nodeExpanded(node, child);
    }

    @Override
    protected void notExpandable(N node) {
        eventSupervision.observe(new NodeExhaustionEvent<>(node));
        super.notExpandable(node);
    }

    @Override
    protected boolean addLeaf(N node) {
        boolean actuallyAdded = super.addLeaf(node);
        if (actuallyAdded) eventSupervision.observe(new LeafAdditionEvent<>(node));
        return actuallyAdded;
    }

    @Override
    protected boolean removeLeaf(N node) {
        boolean actuallyRemoved = super.removeLeaf(node);
        if (actuallyRemoved) eventSupervision.observe(new LeafRemovalEvent<>(node));
        return actuallyRemoved;
    }

    @Override
    public ComponentSystemAdapter componentSystemAdapter() {
        return componentSystemAdapter;
    }
}
