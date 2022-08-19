package org.processmining.estminer.specpp.datastructures.tree.constraints;

import org.processmining.estminer.specpp.datastructures.tree.heuristic.SubtreeCutoffConstraint;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;

public class CullPostsetChildren extends SubtreeCutoffConstraint<PlaceNode> {

    public CullPostsetChildren(PlaceNode affectedPlaceNode) {
        super(affectedPlaceNode);
    }

}
