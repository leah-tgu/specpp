package org.processmining.estminer.specpp.datastructures.tree.heuristic;

import org.processmining.estminer.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;

public class InterestingnessHeuristics implements HeuristicStrategy<PlaceNode, DoubleScore> {
    @Override
    public DoubleScore computeHeuristic(PlaceNode node) {
        return new DoubleScore(-node.getDepth());
    }
}
