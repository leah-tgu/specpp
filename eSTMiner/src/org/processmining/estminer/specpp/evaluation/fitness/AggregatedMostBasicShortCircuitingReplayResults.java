package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.base.CandidateEvaluation;

public class AggregatedMostBasicShortCircuitingReplayResults implements CandidateEvaluation {

    private final double fittingFraction;

    public AggregatedMostBasicShortCircuitingReplayResults(double fittingFraction) {
        this.fittingFraction = fittingFraction;
    }

    public double getFittingFraction() {
        return fittingFraction;
    }

    public double getUnFittingFraction() {
        return 1.0 - fittingFraction;
    }

}
