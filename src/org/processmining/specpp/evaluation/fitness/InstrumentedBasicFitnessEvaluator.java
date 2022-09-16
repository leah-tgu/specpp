package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.supervision.observations.performance.TimeStopper;

public class InstrumentedBasicFitnessEvaluator extends AbstractBasicFitnessEvaluator {
    private final AbstractBasicFitnessEvaluator delegate;
    private final TimeStopper timeStopper = new TimeStopper();

    public InstrumentedBasicFitnessEvaluator(AbstractBasicFitnessEvaluator delegate) {
        super(delegate.replayComputationParameters);
        this.delegate = delegate;
    }

    @Override
    protected BasicFitnessEvaluation basicComputation(Place place, BitMask consideredVariants) {
        timeStopper.start(BASIC_EVALUATION);
        BasicFitnessEvaluation evaluation = delegate.basicComputation(place, consideredVariants);
        timeStopper.stop(BASIC_EVALUATION);
        return evaluation;
    }

    @Override
    protected DetailedFitnessEvaluation detailedComputation(Place place, BitMask consideredVariants) {
        timeStopper.start(DETAILED_EVALUATION);
        DetailedFitnessEvaluation evaluation = delegate.detailedComputation(place, consideredVariants);
        timeStopper.stop(DETAILED_EVALUATION);
        return evaluation;
    }
}
