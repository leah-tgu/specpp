package org.processmining.estminer.specpp.representations.tree.constraints;

import org.processmining.estminer.specpp.est.PlaceNode;
import org.processmining.estminer.specpp.representations.tree.heuristic.LocalNodeGenerationConstraint;

public class CullPostsetChildren extends LocalNodeGenerationConstraint<PlaceNode> {

    public CullPostsetChildren(PlaceNode affectedPlaceNode) {
        super(affectedPlaceNode);
    }

}
