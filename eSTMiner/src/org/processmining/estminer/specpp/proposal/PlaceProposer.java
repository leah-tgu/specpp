package org.processmining.estminer.specpp.proposal;

import org.processmining.estminer.specpp.base.impls.GeneratingTreeProposer;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.componenting.traits.UsesComponentSystem;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.base.*;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.supervision.instrumentators.InstrumentedProposer;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;
import org.processmining.estminer.specpp.base.Proposer;

/**
 * The base implementation of a {@code Proposer} for candidates of type {@code Place}.
 * It internally uses an {@code EnumeratingTree} to deterministically propose all valid place candidates that the underlying tree provides.
 * The tree itself uses an {@code ExpansionStrategy} to determine which nodes to expand next and this class's {@code ConstrainableLocalNodeGenerator} to calculate the child nodes.
 *
 * @see Proposer
 * @see Place
 * @see EnumeratingTree
 * @see ConstrainableLocalNodeGenerator
 */
public class PlaceProposer<G extends PlaceGenerator> extends GeneratingTreeProposer<Place, PlaceNode, G> {


    public PlaceProposer(G generator, EfficientTree<PlaceNode> tree) {
        super(generator, tree);
    }

    @Override
    protected boolean describesValidCandidate(PlaceNode node) {
        return node.getProperties().size() >= 2;
    }


}
