package org.processmining.estminer.specpp.proposal;

import org.processmining.estminer.specpp.base.Proposer;
import org.processmining.estminer.specpp.base.impls.GeneratingTreeProposer;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.base.ConstrainableLocalNodeGenerator;
import org.processmining.estminer.specpp.datastructures.tree.base.EfficientTree;
import org.processmining.estminer.specpp.datastructures.tree.base.PlaceGenerator;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;

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
