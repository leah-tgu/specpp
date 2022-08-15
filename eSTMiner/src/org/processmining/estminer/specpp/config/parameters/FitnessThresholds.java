package org.processmining.estminer.specpp.config.parameters;

import org.processmining.estminer.specpp.evaluation.fitness.BasicVariantFitnessStatus;
import org.processmining.estminer.specpp.evaluation.fitness.DerivedVariantFitnessStatus;

import java.util.Arrays;

public class FitnessThresholds implements Parameters {
    private final double[] thresholds;

    public static FitnessThresholds tau(double t) {
        return new FitnessThresholds(t, 1 - t, 1 - t, 1 - t);
    }

    public static FitnessThresholds unsafe_feasible(double tau) {
        return new FitnessThresholds(tau, 1 - tau, tau, 1 - tau);
    }

    public FitnessThresholds(double fittingFractionThreshold, double goesNegativeFractionThreshold, double nonSafeFractionThreshold, double notEndingOnZeroThreshold) {
        thresholds = new double[]{fittingFractionThreshold, goesNegativeFractionThreshold, nonSafeFractionThreshold, notEndingOnZeroThreshold};
        assert thresholds.length >= BasicVariantFitnessStatus.values().length;
    }

    public double getThreshold(BasicVariantFitnessStatus basicVariantFitnessStatus) {
        return thresholds[basicVariantFitnessStatus.ordinal()];
    }


    public double getThreshold(DerivedVariantFitnessStatus derivedVariantFitnessStatus) {
        return derivedVariantFitnessStatus.thresholdFunc.applyAsDouble(this);
    }

    @Override
    public String toString() {
        return "FitnessThresholds(" + Arrays.toString(thresholds) + ')';
    }

}
