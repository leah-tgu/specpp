package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.datastructures.util.EnumFractions;

public class SimplifiedFitnessEvaluation extends EnumFractions<SimplifiedFitnessStatus> {

    public SimplifiedFitnessEvaluation(double fittingFraction, double underfedFraction, double overfedFraction) {
        super(new double[]{fittingFraction, underfedFraction, overfedFraction, underfedFraction + overfedFraction});
    }

}
