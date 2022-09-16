package org.processmining.specpp.evaluation.heuristics;

import org.processmining.specpp.config.parameters.Parameters;
import org.processmining.specpp.datastructures.tree.heuristic.DoubleScore;

public class TreeHeuristicThreshold implements Parameters {
    private final DoubleScore lambda;

    public TreeHeuristicThreshold(double lambda) {
        this.lambda = new DoubleScore(lambda);
    }

    public static TreeHeuristicThreshold getDefault() {
        return new TreeHeuristicThreshold(0);
    }

    public DoubleScore getLambda() {
        return lambda;
    }


    @Override
    public String toString() {
        return "HeuristicThresholdParameters{" + "lambda=" + lambda + '}';
    }
}
