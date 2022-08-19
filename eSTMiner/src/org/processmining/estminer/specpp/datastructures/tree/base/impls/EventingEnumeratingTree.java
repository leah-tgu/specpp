package org.processmining.estminer.specpp.datastructures.tree.base.impls;

import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.traits.UsesGlobalComponentSystem;
import org.processmining.estminer.specpp.datastructures.tree.base.ExpansionStrategy;
import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.estminer.specpp.datastructures.tree.events.*;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.supervision.piping.AsyncAdHocObservableWrapper;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;

public class EventingEnumeratingTree<N extends TreeNode & LocallyExpandable<N>> extends EnumeratingTree<N> implements UsesGlobalComponentSystem {


    private final ComponentCollection componentSystemAdapter = new ComponentCollection();

    private final EventSupervision<TreeEvent> eventSupervision = PipeWorks.eventSupervision();

    protected final TimeStopper timeStopper = new TimeStopper();

    public EventingEnumeratingTree(N root, ExpansionStrategy<N> expansionStrategy) {
        super(root, expansionStrategy);
        makeProvisions();
    }

    public EventingEnumeratingTree(ExpansionStrategy<N> expansionStrategy) {
        super(expansionStrategy);
        makeProvisions();
    }

    protected void makeProvisions() {
        componentSystemAdapter.provide(SupervisionRequirements.observable("tree.events", TreeEvent.class, eventSupervision))
                              .provide(SupervisionRequirements.adHocObservable("tree.stats", EnumeratingTreeStatsEvent.class, AsyncAdHocObservableWrapper.wrap(() -> new EnumeratingTreeStatsEvent(leaves.size()))));
    }

    @Override
    protected N expand() {
        return super.expand();
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
    public ComponentCollection componentSystemAdapter() {
        return componentSystemAdapter;
    }
}
