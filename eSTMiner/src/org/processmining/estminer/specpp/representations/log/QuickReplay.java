package org.processmining.estminer.specpp.representations.log;

import org.processmining.estminer.specpp.representations.BitMask;
import org.processmining.estminer.specpp.representations.encoding.BitEncodedSet;
import org.processmining.estminer.specpp.representations.encoding.IndexSubset;
import org.processmining.estminer.specpp.representations.log.impls.DenseVariantMarkingHistories;
import org.processmining.estminer.specpp.representations.log.impls.EncodedLog;
import org.processmining.estminer.specpp.representations.log.impls.MultiEncodedLog;
import org.processmining.estminer.specpp.representations.petri.Place;
import org.processmining.estminer.specpp.representations.petri.Transition;
import org.processmining.estminer.specpp.representations.vectorization.IVSComputations;
import org.processmining.estminer.specpp.representations.vectorization.IntVectorStorage;

import java.util.function.IntUnaryOperator;

public class QuickReplay {

    public static IntUnaryOperator presetIndicator(final Place place) {
        final BitEncodedSet<Transition> preset = place.preset();
        return i -> preset.containsIndex(i) ? 1 : 0;
    }

    public static IntUnaryOperator postsetIndicator(final Place place) {
        final BitEncodedSet<Transition> postset = place.postset();
        return i -> postset.containsIndex(i) ? -1 : 0;
    }


    public static DenseVariantMarkingHistories makeHistoryOn(BitMask interestingVariants, MultiEncodedLog multiEncodedLog, Place place) {
        assert multiEncodedLog.variantIndices().isSupersetOf(interestingVariants);
        EncodedLog pre = multiEncodedLog.pre(), post = multiEncodedLog.post();
        IntVectorStorage interleft = IVSComputations.interleaveOn(interestingVariants, post.getEncodedVariantVectors(), postsetIndicator(place), pre.getEncodedVariantVectors(), presetIndicator(place));
        return new DenseVariantMarkingHistories(IndexSubset.of(interestingVariants), IVSComputations.vectorwiseCumulation(interleft));
    }

    public static DenseVariantMarkingHistories makeHistory(MultiEncodedLog data, Place input) {
        return makeHistoryOn(data.variantIndices(), data, input);
    }
}
