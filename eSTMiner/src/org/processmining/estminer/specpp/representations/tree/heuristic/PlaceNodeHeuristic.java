package org.processmining.estminer.specpp.representations.tree.heuristic;

import org.processmining.estminer.specpp.traits.ZeroOneBounded;

public class PlaceNodeHeuristic extends DoubleScore implements ZeroOneBounded {

    public PlaceNodeHeuristic(double score) {
        super(score);
    }


}
