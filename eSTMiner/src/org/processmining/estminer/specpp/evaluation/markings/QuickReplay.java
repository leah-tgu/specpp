package org.processmining.estminer.specpp.evaluation.markings;

import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.IndexSubset;
import org.processmining.estminer.specpp.datastructures.log.impls.DenseVariantMarkingHistories;
import org.processmining.estminer.specpp.datastructures.log.impls.EncodedLog;
import org.processmining.estminer.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.vectorization.IVSComputations;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorStorage;
import org.processmining.estminer.specpp.evaluation.fitness.ForkJoinFitnessEvaluator;

public class QuickReplay {


    public static DenseVariantMarkingHistories makeHistoryOn(BitMask interestingVariants, MultiEncodedLog multiEncodedLog, Place place) {
        assert multiEncodedLog.variantIndices().isSupersetOf(interestingVariants);
        EncodedLog pre = multiEncodedLog.pre(), post = multiEncodedLog.post();
        IntVectorStorage interleft = IVSComputations.interleaveOn(interestingVariants, post.getEncodedVariantVectors(), ForkJoinFitnessEvaluator.postsetIndicator(place), pre.getEncodedVariantVectors(), ForkJoinFitnessEvaluator.presetIndicator(place));
        return new DenseVariantMarkingHistories(IndexSubset.of(interestingVariants), IVSComputations.vectorwiseCumulation(interleft));
    }

    public static DenseVariantMarkingHistories makeHistory(MultiEncodedLog data, Place input) {
        return makeHistoryOn(data.variantIndices(), data, input);
    }
}
