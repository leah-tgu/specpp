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

    public double getFraction(BasicVariantFitnessStatus basicVariantFitnessStatus) {
        return fractions[basicVariantFitnessStatus.ordinal()];
    }

    public double getFraction(DerivedVariantFitnessStatus derivedVariantFitnessStatus) {
        return derivedVariantFitnessStatus.measurementFunc.applyAsDouble(this);
    }

}
