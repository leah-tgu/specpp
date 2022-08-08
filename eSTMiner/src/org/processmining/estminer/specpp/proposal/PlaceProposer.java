package org.processmining.estminer.specpp.proposal;

import org.processmining.estminer.specpp.base.impls.GeneratingTreeProposer;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.componenting.traits.UsesComponentSystem;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.base.ConstrainableLocalNodeGenerator;
import org.processmining.estminer.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;
import org.processmining.estminer.specpp.base.Proposer;

/**
 * The base implementation of a {@code Proposer} for candidates of type {@code Place}.
 * It internally uses an {@code EnumeratingTree} to deterministically propose all valid place candidates that the underlying tree provides.
 * The tree itself uses an {@code ExpansionStrategy} to determine which nodes to expand next and this class's {@code ConstrainableLocalNodeGenerator} to calculate the child nodes.
 * This class participates in the componenting system to provide {@code PerformanceEvent} measurements for {@code proposeCandidate()}.
 *
 * @see Proposer
 * @see Place
 * @see EnumeratingTree
 * @see ConstrainableLocalNodeGenerator
 */
public class PlaceProposer extends GeneratingTreeProposer<Place, PlaceNode, ConstrainableLocalNodeGenerator<Place, ?, PlaceNode, GenerationConstraint>> implements UsesComponentSystem {

    protected final ComponentSystemAdapter componentSystemAdapter = new ComponentSystemAdapter();

    protected final TimeStopper timeStopper = new TimeStopper();

    public PlaceProposer(ConstrainableLocalNodeGenerator<Place, ?, PlaceNode, GenerationConstraint> generator, EnumeratingTree<PlaceNode> tree) {
        super(generator, tree);
        componentSystemAdapter().provide(SupervisionRequirements.observable("proposer.performance", PerformanceEvent.class, timeStopper));
    }

    @Override
    public Place proposeCandidate() {
        timeStopper.start(TaskDescription.CANDIDATE_PROPOSAL);
        Place candidate = super.proposeCandidate();
        timeStopper.stop(TaskDescription.CANDIDATE_PROPOSAL);
        return candidate;
    }

    @Override
    protected boolean describesValidCandidate(PlaceNode node) {
        return node.getProperties().size() >= 2;
    }


    @Override
    public ComponentSystemAdapter componentSystemAdapter() {
        return componentSystemAdapter;
    }
}
