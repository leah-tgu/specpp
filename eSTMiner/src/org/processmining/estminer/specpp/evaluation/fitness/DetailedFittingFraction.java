package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.base.CandidateEvaluation;
import org.processmining.estminer.specpp.datastructures.encoding.BitMask;

public class DetailedFittingFraction implements CandidateEvaluation {

    private final BitMask fittingVariants;
    private final double fittingFraction;

    public DetailedFittingFraction(BitMask fittingVariants, double fittingFraction) {
        this.fittingVariants = fittingVariants;
        this.fittingFraction = fittingFraction;
    }

    public BitMask getFittingVariants() {
        return fittingVariants;
    }

    public double getFittingFraction() {
        return fittingFraction;
    }
}
