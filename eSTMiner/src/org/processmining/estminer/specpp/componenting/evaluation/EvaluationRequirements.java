package org.processmining.estminer.specpp.componenting.evaluation;

import org.processmining.estminer.specpp.base.Evaluable;
import org.processmining.estminer.specpp.base.Evaluation;
import org.processmining.estminer.specpp.base.Evaluator;
import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.log.impls.DenseVariantMarkingHistories;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.estminer.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.estminer.specpp.evaluation.fitness.AggregatedBasicFitnessEvaluation;
import org.processmining.estminer.specpp.evaluation.fitness.BasicFitnessEvaluation;
import org.processmining.estminer.specpp.evaluation.fitness.DetailedFitnessEvaluation;
import org.processmining.estminer.specpp.evaluation.fitness.FullBasicFitnessEvaluation;
import org.processmining.estminer.specpp.evaluation.implicitness.ImplicitnessRating;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

public class EvaluationRequirements {

    public static final EvaluatorRequirement<Place, BasicFitnessEvaluation> BASIC_FITNESS = evaluator(Place.class, BasicFitnessEvaluation.class);
    public static final EvaluatorRequirement<Place, DetailedFitnessEvaluation> DETAILED_FITNESS = evaluator(Place.class, DetailedFitnessEvaluation.class);

    public static final EvaluatorRequirement<EvaluationParameterTuple2<Place, BitMask>, BasicFitnessEvaluation> SUBSET_BASIC_FITNESS = evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), BasicFitnessEvaluation.class);
    public static final EvaluatorRequirement<Place, AggregatedBasicFitnessEvaluation> AGG_PLACE_FITNESS = evaluator(Place.class, AggregatedBasicFitnessEvaluation.class);
    public static final EvaluatorRequirement<Place, FullBasicFitnessEvaluation> FULL_PLACE_FITNESS = evaluator(Place.class, FullBasicFitnessEvaluation.class);
    public static final EvaluatorRequirement<Place, ImplicitnessRating> PLACE_IMPLICITNESS = evaluator(Place.class, ImplicitnessRating.class);
    public static final EvaluatorRequirement<Place, DenseVariantMarkingHistories> PLACE_MARKING_HISTORY = evaluator(Place.class, DenseVariantMarkingHistories.class);
    public static final EvaluatorRequirement<Place, ? super DenseVariantMarkingHistories> PURE_PLACE_MARKING_HISTORY = evaluator(Place.class, DenseVariantMarkingHistories.class);
    public static final EvaluatorRequirement<EvaluationParameterTuple2<Place, Integer>, DoubleScore> DELTA_ADAPTATION_FUNCTION = evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), DoubleScore.class);


    public static <I extends Evaluable, E extends Evaluation> EvaluatorRequirement<I, E> evaluator(Class<I> evaluableClass, Class<E> evaluationClass) {
        return new EvaluatorRequirement<>(evaluableClass, evaluationClass);
    }

    public static <I extends Evaluable, E extends Evaluation> FulfilledEvaluatorRequirement<I, E> evaluator(Class<I> evaluableClass, Class<E> evaluationClass, Evaluator<I, E> evaluator) {
        return evaluator(evaluableClass, evaluationClass).fulfilWith(evaluator);
    }

    public static <I extends Evaluable, E extends Evaluation> FulfilledEvaluatorRequirement<I, E> evaluator(EvaluatorRequirement<I, E> requirement, Evaluator<I, E> evaluator) {
        return requirement.fulfilWith(evaluator);
    }

}
