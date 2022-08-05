package org.processmining.estminer.specpp.datastructures.tree.heuristic;

import org.processmining.estminer.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;

public class InterestingnessHeuristics implements HeuristicStrategy<PlaceNode, DoubleScore> {
    @Override
    public PlaceNodeHeuristic computeHeuristic(PlaceNode node) {
        return new PlaceNodeHeuristic(-node.getDepth());
    }
}
