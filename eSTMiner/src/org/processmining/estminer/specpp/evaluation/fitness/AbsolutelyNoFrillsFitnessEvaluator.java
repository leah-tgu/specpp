package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.base.Evaluable;
import org.processmining.estminer.specpp.base.Evaluation;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.util.EnumCounts;
import org.processmining.estminer.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.estminer.specpp.datastructures.util.IndexedItem;
import org.processmining.estminer.specpp.datastructures.util.Tuple2;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVector;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

import java.nio.IntBuffer;
import java.util.Spliterator;
import java.util.function.IntUnaryOperator;

public class AbsolutelyNoFrillsFitnessEvaluator extends AbstractFitnessEvaluator {
    public AbsolutelyNoFrillsFitnessEvaluator() {
        componentSystemAdapter().provide(EvaluationRequirements.evaluator(Place.class, SimplestFitnessEvaluation.class, this::eval));
        componentSystemAdapter().provide(EvaluationRequirements.evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), SimplestFitnessEvaluation.class, this::subsetEval));
    }

    private SimplestFitnessEvaluation subsetEval(EvaluationParameterTuple2<Place, BitMask> tuple) {
        return eval(tuple.getT1(), tuple.getT2());
    }


    public SimplestFitnessEvaluation eval(Place place) {
        return eval(place, getConsideredVariants());
    }

    public SimplestFitnessEvaluation eval(Place place, BitMask consideredVariants) {
        timeStopper.start(TaskDescription.SIMPLEST_EVALUATION);

        IntUnaryOperator presetIndicator = ReplayUtils.presetIndicator(place);
        IntUnaryOperator postsetIndicator = ReplayUtils.postsetIndicator(place);
        MultiEncodedLog multiEncodedLog = getMultiEncodedLog();
        Spliterator<IndexedItem<Tuple2<IntBuffer, IntBuffer>>> spliterator = multiEncodedLog.efficientIndexedSpliterator();
        IntVector frequencies = multiEncodedLog.getPresetEncodedLog().getVariantFrequencies();

        int[] counts = new int[ReplayUtils.ReplayOutcomes.values().length];
        spliterator.forEachRemaining(ii -> {
            if (consideredVariants == null || consideredVariants.get(ii.getIndex())) {
                Tuple2<IntBuffer, IntBuffer> t = ii.getItem();
                IntBuffer presetEncodedVariant = t.getT1(), postsetEncodedVariant = t.getT2();
                int acc = 0;
                boolean wentUnder = false, wentOver = false;
                while (presetEncodedVariant.hasRemaining() && postsetEncodedVariant.hasRemaining()) {
                    int i = presetIndicator.applyAsInt(presetEncodedVariant.get());
                    acc += i;
                    wentUnder |= acc < 0;
                    int j = postsetIndicator.applyAsInt(postsetEncodedVariant.get());
                    acc += j;
                    wentOver |= acc > 1;
                }
                boolean notZeroAtEnd = acc > 0;
                int c = frequencies.get(ii.getIndex());
                if (!notZeroAtEnd && !wentUnder && !wentOver) counts[ReplayUtils.ReplayOutcomes.FITTING.ordinal()] += c;
                else {
                    counts[ReplayUtils.ReplayOutcomes.NON_FITTING.ordinal()] += c;
                    if (notZeroAtEnd) {
                        counts[ReplayUtils.ReplayOutcomes.OVERFED.ordinal()] += c;
                        counts[ReplayUtils.ReplayOutcomes.NOT_ENDING_ON_ZERO.ordinal()] += c;
                    }
                    if (wentOver) counts[ReplayUtils.ReplayOutcomes.WENT_ABOVE_ONE.ordinal()] += c;
                    if (wentUnder) counts[ReplayUtils.ReplayOutcomes.WENT_NEGATIVE.ordinal()] += c;
                }
            }
        });

        EnumCounts<ReplayUtils.ReplayOutcomes> enumCounts = new EnumCounts<>(counts);
        SimplestFitnessEvaluation evaluation = ReplayUtils.summarizeInto(enumCounts);
        timeStopper.stop(TaskDescription.SIMPLEST_EVALUATION);
        return evaluation;
    }

}
