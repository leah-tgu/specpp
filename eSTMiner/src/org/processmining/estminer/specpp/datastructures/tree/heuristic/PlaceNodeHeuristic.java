package org.processmining.estminer.specpp.datastructures.tree.heuristic;

import org.processmining.estminer.specpp.traits.ZeroOneBounded;

public class PlaceNodeHeuristic extends DoubleScore implements ZeroOneBounded {

    public PlaceNodeHeuristic(double score) {
        super(score);
    }


}
