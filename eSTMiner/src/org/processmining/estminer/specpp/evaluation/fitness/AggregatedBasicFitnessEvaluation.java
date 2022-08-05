package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.base.CandidateEvaluation;
import org.processmining.estminer.specpp.base.Evaluable;
import org.processmining.estminer.specpp.datastructures.util.EnumFractions;

public class AggregatedBasicFitnessEvaluation extends EnumFractions<BasicVariantFitnessStatus> implements CandidateEvaluation, Evaluable {


    public static AggregatedBasicFitnessEvaluation zero() {
        return new AggregatedBasicFitnessEvaluation(new double[BasicVariantFitnessStatus.values().length]);
    }

    public AggregatedBasicFitnessEvaluation(double[] fractions) {
        super(fractions);
        assert fractions.length == BasicVariantFitnessStatus.values().length;
    }

    public double getUnderfedFraction() {
        return getFraction(BasicVariantFitnessStatus.UNDERFED);
    }

    public double getOverfedFraction() {
        return getFraction(BasicVariantFitnessStatus.OVERFED);
    }

    public double getNotEndingOnZeroFraction() {
        return getFraction(BasicVariantFitnessStatus.NOT_ENDING_ON_ZERO);
    }

    public double getFittingFraction() {
        return getFraction(BasicVariantFitnessStatus.FITTING);
    }

}
