package org.processmining.estminer.specpp.proposal;

import org.processmining.estminer.specpp.base.Proposer;
import org.processmining.estminer.specpp.base.impls.EfficientTreeWithExternalizedLogicBasedProposer;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.base.ConstrainableChildGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.base.EfficientTree;
import org.processmining.estminer.specpp.datastructures.tree.base.PlaceGenerationLogic;
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
 * @see ConstrainableChildGenerationLogic
 */
public class PlaceProposer<G extends PlaceGenerationLogic> extends EfficientTreeWithExternalizedLogicBasedProposer<Place, PlaceNode, G> {

    public PlaceProposer(G generator, EfficientTree<PlaceNode> tree) {
        super(generator, tree);
    }

    @Override
    protected boolean describesValidCandidate(PlaceNode node) {
        return node.getPlace().size() >= 2;
    }

    @Override
    protected Place extractCandidate(PlaceNode node) {
        return node.getPlace();
    }

    @Override
    protected void initSelf() {
        tree.setRootOnce(generationLogic.generateRoot());
    }

}
