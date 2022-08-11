package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.config.parameters.FitnessThresholds;

import java.util.function.ToDoubleFunction;

public enum DerivedVariantFitnessStatus {

    FEASIBLE(t -> Math.max(t.getThreshold(BasicVariantFitnessStatus.FITTING), t.getThreshold(BasicVariantFitnessStatus.NON_SAFE)), f -> f.getFraction(BasicVariantFitnessStatus.FITTING) + f.getFraction(BasicVariantFitnessStatus.NON_SAFE));

    public final ToDoubleFunction<FitnessThresholds> thresholdFunc;
    public final ToDoubleFunction<AggregatedBasicFitnessEvaluation> measurementFunc;

    DerivedVariantFitnessStatus(ToDoubleFunction<FitnessThresholds> thresholdFunc, ToDoubleFunction<AggregatedBasicFitnessEvaluation> measurementFunc) {
        this.thresholdFunc = thresholdFunc;
        this.measurementFunc = measurementFunc;
    }
}
