package org.processmining.estminer.specpp.proposal;

import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.componenting.traits.UsesComponentSystem;
import org.processmining.estminer.specpp.est.PlaceNode;
import org.processmining.estminer.specpp.representations.petri.Place;
import org.processmining.estminer.specpp.representations.tree.base.ConstrainableLocalNodeGenerator;
import org.processmining.estminer.specpp.representations.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.representations.tree.base.impls.EnumeratingTree;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;

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
