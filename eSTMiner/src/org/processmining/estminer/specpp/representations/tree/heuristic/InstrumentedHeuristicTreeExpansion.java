package org.processmining.estminer.specpp.representations.tree.heuristic;

import org.processmining.estminer.specpp.base.Evaluable;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.componenting.traits.UsesComponentSystem;
import org.processmining.estminer.specpp.representations.tree.base.HeuristicStrategy;
import org.processmining.estminer.specpp.representations.tree.base.TreeNode;
import org.processmining.estminer.specpp.representations.tree.base.traits.LocallyExpandable;
import org.processmining.estminer.specpp.representations.tree.events.DequeueNodeEvent;
import org.processmining.estminer.specpp.representations.tree.events.EnqueueNodeEvent;
import org.processmining.estminer.specpp.representations.tree.events.HeuristicComputationEvent;
import org.processmining.estminer.specpp.representations.tree.events.TreeHeuristicsEvent;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.supervision.observations.HeuristicStatsEvent;
import org.processmining.estminer.specpp.supervision.piping.AsyncAdHocObservableWrapper;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

public class InstrumentedHeuristicTreeExpansion<N extends TreeNode & Evaluable & LocallyExpandable<N>, H extends NodeHeuristic<H>> extends HeuristicTreeExpansion<N, H> implements UsesComponentSystem {

    private final EventSupervision<TreeHeuristicsEvent> eventSupervision = PipeWorks.eventSupervision();

    private final ComponentSystemAdapter componentSystemAdapter = new ComponentSystemAdapter();

    public InstrumentedHeuristicTreeExpansion(HeuristicStrategy<N, H> heuristicStrategy) {
        super(heuristicStrategy);
        componentSystemAdapter.provide(SupervisionRequirements.observable("heuristics.events", JavaTypingUtils.castClass(HeuristicComputationEvent.class), eventSupervision))
                              .provide(SupervisionRequirements.adHocObservable("heuristics.stats", HeuristicStatsEvent.class, AsyncAdHocObservableWrapper.wrap(() -> new HeuristicStatsEvent(priorityQueue.size()))));
    }

    @Override
    protected void putHeuristic(N node, H heuristic) {
        super.putHeuristic(node, heuristic);
        eventSupervision.observe(new HeuristicComputationEvent<>(node, heuristic));
    }

    @Override
    protected void enqueue(N node) {
        super.enqueue(node);
        eventSupervision.observe(new EnqueueNodeEvent<>(node));
    }

    @Override
    protected void dequeue(N node) {
        super.dequeue(node);
        eventSupervision.observe(new DequeueNodeEvent<>(node));
    }

    @Override
    protected N dequeueFirst() {
        N n = super.dequeueFirst();
        eventSupervision.observe(new DequeueNodeEvent<>(n));
        return n;
    }


    @Override
    public ComponentSystemAdapter componentSystemAdapter() {
        return componentSystemAdapter;
    }

}
