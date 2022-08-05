package org.processmining.estminer.specpp.datastructures.tree.constraints;

import org.processmining.estminer.specpp.datastructures.tree.heuristic.LocalNodeGenerationConstraint;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;

public class CullPostsetChildren extends LocalNodeGenerationConstraint<PlaceNode> {

    public CullPostsetChildren(PlaceNode affectedPlaceNode) {
        super(affectedPlaceNode);
    }

}
