package org.processmining.specpp.datastructures.tree.heuristic;

import org.apache.commons.lang3.NotImplementedException;
import org.processmining.specpp.datastructures.tree.base.HeuristicStrategy;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;

public class InterestingnessHeuristic implements HeuristicStrategy<PlaceNode, DoubleScore> {
    @Override
    public DoubleScore computeHeuristic(PlaceNode node) {
        throw new NotImplementedException();
        //return new DoubleScore(-node.getDepth());
    }
}
