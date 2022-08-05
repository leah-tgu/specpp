package org.processmining.estminer.specpp.evaluation;

import org.processmining.estminer.specpp.util.datastructures.EnumFractions;

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
