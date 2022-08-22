package org.processmining.estminer.specpp.datastructures.tree.constraints;

import org.processmining.estminer.specpp.datastructures.tree.heuristic.SubtreeCutoffConstraint;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;

public class CullPresetChildren extends SubtreeCutoffConstraint<PlaceNode> {
    public CullPresetChildren(PlaceNode affectedNode) {
        super(affectedNode);
    }
}
