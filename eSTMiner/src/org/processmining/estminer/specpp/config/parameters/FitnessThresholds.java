package org.processmining.estminer.specpp.config.parameters;

import org.processmining.estminer.specpp.evaluation.fitness.BasicVariantFitnessStatus;

import java.util.Arrays;

public class FitnessThresholds implements Parameters {

    public static FitnessThresholds ACCEPT_ALL_NON_COMPLETELY_UNDERFED = exhaustive(0);

    private final double[] thresholds;

    public static FitnessThresholds strictUnderfedCulling(double fullyFittingThreshold) {
        return new FitnessThresholds(fullyFittingThreshold, 1e-8, 0, 0);
    }

    public static FitnessThresholds tau(double t) {
        return new FitnessThresholds(t, 1, 0, 0);
    }

    public static FitnessThresholds exhaustive(double fullyFittingThreshold) {
        return new FitnessThresholds(fullyFittingThreshold, 1, 0, 0);
    }

    public FitnessThresholds(double fittingFractionThreshold, double goesNegativeFractionThreshold, double nonSafeFractionThreshold, double notEndingOnZeroThreshold) {
        thresholds = new double[]{fittingFractionThreshold, goesNegativeFractionThreshold, nonSafeFractionThreshold, notEndingOnZeroThreshold};
        assert thresholds.length >= BasicVariantFitnessStatus.values().length;
    }

    public double getThreshold(BasicVariantFitnessStatus basicVariantFitnessStatus) {
        return thresholds[basicVariantFitnessStatus.ordinal()];
    }

    @Override
    public String toString() {
        return "FitnessThresholds{" + "thresholds=" + Arrays.toString(thresholds) + '}';
    }
}
