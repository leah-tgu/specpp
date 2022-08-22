package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.util.EnumCounts;
import org.processmining.estminer.specpp.datastructures.util.IndexedItem;
import org.processmining.estminer.specpp.datastructures.util.Tuple2;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVector;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;

import java.nio.IntBuffer;
import java.util.Spliterator;
import java.util.function.IntUnaryOperator;

@SuppressWarnings("duplication")
public class AbsolutelyNoFrillsFitnessEvaluator extends AbstractBasicFitnessEvaluator {

    @Override
    public BasicFitnessEvaluation basicComputation(Place place, BitMask consideredVariants) {
        timeStopper.start(TaskDescription.BASIC_EVALUATION);
        int[] counts = ReplayUtils.getCounts();
        ResultUpdater upd = (idx, c, wentUnder, wentOver, notZeroAtEnd) -> ReplayUtils.updateCounts(counts, wentUnder, wentOver, notZeroAtEnd, c);

        run(consideredVariants, place, upd);

        EnumCounts<ReplayUtils.ReplayOutcomes> enumCounts = new EnumCounts<>(counts);
        BasicFitnessEvaluation evaluation = ReplayUtils.summarizeInto(enumCounts);
        timeStopper.stop(TaskDescription.BASIC_EVALUATION);
        return evaluation;
    }

    @Override
    public DetailedFitnessEvaluation detailedComputation(Place place, BitMask consideredVariants) {
        timeStopper.start(TaskDescription.DETAILED_EVALUATION);

        BitMask bm = new BitMask();
        int[] counts = ReplayUtils.getCounts();
        ResultUpdater upd = (idx, c, wentUnder, wentOver, notZeroAtEnd) -> {
            ReplayUtils.updateFittingVariantMask(bm, wentUnder, wentOver, notZeroAtEnd, idx);
            ReplayUtils.updateCounts(counts, wentUnder, wentOver, notZeroAtEnd, c);
        };

        run(consideredVariants, place, upd);

        EnumCounts<ReplayUtils.ReplayOutcomes> enumCounts = new EnumCounts<>(counts);
        BasicFitnessEvaluation evaluation = ReplayUtils.summarizeInto(enumCounts);
        DetailedFitnessEvaluation res = new DetailedFitnessEvaluation(bm, evaluation);
        timeStopper.stop(TaskDescription.DETAILED_EVALUATION);
        return res;
    }

    private void run(BitMask consideredVariants, Place place, ResultUpdater upd) {
        IntUnaryOperator presetIndicator = ReplayUtils.presetIndicator(place);
        IntUnaryOperator postsetIndicator = ReplayUtils.postsetIndicator(place);
        Spliterator<IndexedItem<Tuple2<IntBuffer, IntBuffer>>> spliterator = getIndexedItemSpliterator();
        IntVector frequencies = getVariantFrequencies();
        spliterator.forEachRemaining(ii -> {
            if (consideredVariants == null || consideredVariants.get(ii.getIndex())) {
                Tuple2<IntBuffer, IntBuffer> t = ii.getItem();
                IntBuffer presetEncodedVariant = t.getT1(), postsetEncodedVariant = t.getT2();
                int acc = 0;
                boolean wentUnder = false, wentOver = false;
                while (presetEncodedVariant.hasRemaining() && postsetEncodedVariant.hasRemaining()) {
                    int i = postsetIndicator.applyAsInt(postsetEncodedVariant.get());
                    acc += i;
                    wentUnder |= acc < 0;
                    int j = presetIndicator.applyAsInt(presetEncodedVariant.get());
                    acc += j;
                    wentOver |= acc > 1;
                }
                boolean notZeroAtEnd = acc > 0;
                int idx = ii.getIndex();
                int c = frequencies.get(idx);
                upd.update(idx, c, wentUnder, wentOver, notZeroAtEnd);
            }
        });
    }

    @FunctionalInterface
    private interface ResultUpdater {

        void update(int variantIndex, int variantFrequency, boolean wentUnder, boolean wentOver, boolean notZeroAtEnd);

    }


}
