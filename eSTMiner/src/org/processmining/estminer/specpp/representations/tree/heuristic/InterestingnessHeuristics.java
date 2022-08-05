package org.processmining.estminer.specpp.representations.tree.heuristic;

import org.processmining.estminer.specpp.est.PlaceNode;
import org.processmining.estminer.specpp.representations.tree.base.HeuristicStrategy;

public class InterestingnessHeuristics implements HeuristicStrategy<PlaceNode, DoubleScore> {
    @Override
    public PlaceNodeHeuristic computeHeuristic(PlaceNode node) {
        return new PlaceNodeHeuristic(-node.getDepth());
    }
}
