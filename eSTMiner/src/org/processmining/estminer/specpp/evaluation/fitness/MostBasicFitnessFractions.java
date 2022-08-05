package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.datastructures.util.EnumFractions;

public class MostBasicFitnessFractions extends EnumFractions<MostBasicFitness> {


    public MostBasicFitnessFractions(double[] fractions) {
        super(fractions);
    }

    public double getReplayableVariantFraction() {
        return getFraction(MostBasicFitness.FITTING);
    }

    public double getUnderfedVariantFraction() {
        return getFraction(MostBasicFitness.UNDERFED);
    }

}
