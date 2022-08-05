package org.processmining.estminer.specpp.evaluation;

import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.system.AbstractComponentSystemUser;

public class FitnessSummarizer extends AbstractComponentSystemUser {

    public FitnessSummarizer() {
        componentSystemAdapter().provide(EvaluationRequirements.evaluator(AggregatedBasicFitnessEvaluation.class, AggregatedShortCircuitedFitness.class, this::summarize));
    }

    public AggregatedShortCircuitedFitness summarize(AggregatedBasicFitnessEvaluation input) {
        if (input.getFittingFraction() >= 1.0) return AggregatedShortCircuitedFitness.FITTING;
        else if (input.getUnderfedFraction() >= 1.0) return AggregatedShortCircuitedFitness.UNDERFED;
        else if (input.getOverfedFraction() + input.getNotEndingOnZeroFraction() >= 1.0)
            return AggregatedShortCircuitedFitness.OVERFED;
        else return AggregatedShortCircuitedFitness.MALFED;
    }

}
