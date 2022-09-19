package org.processmining.specpp.evaluation.heuristics;

import org.processmining.specpp.config.parameters.Parameters;
import org.processmining.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.specpp.datastructures.vectorization.OrderingRelation;

public class TreeHeuristicThreshold implements Parameters {
    private final DoubleScore lambda;
    private final OrderingRelation comparisonRelation;

    public TreeHeuristicThreshold(double lambda, OrderingRelation comparisonRelation) {
        this.lambda = new DoubleScore(lambda);
        this.comparisonRelation = comparisonRelation;
    }

    public static TreeHeuristicThreshold getDefault() {
        return new TreeHeuristicThreshold(0, OrderingRelation.gtEq);
    }

    public DoubleScore getLambda() {
        return lambda;
    }


    @Override
    public String toString() {
        return "TreeHeuristicThreshold{" + "lambda=" + lambda + ", orderingRelation=" + comparisonRelation + '}';
    }

    public OrderingRelation getComparisonRelation() {
        return comparisonRelation;
    }
}
