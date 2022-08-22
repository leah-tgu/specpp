package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.Transition;
import org.processmining.estminer.specpp.datastructures.util.EnumCounts;

import java.util.EnumSet;
import java.util.function.IntUnaryOperator;

public class ReplayUtils {
    // TODO efficiency improvement opportunity
    public static IntUnaryOperator presetIndicator(final Place place) {
        final BitEncodedSet<Transition> preset = place.preset();
        return i -> preset.containsIndex(i) ? 1 : 0;
    }

    public static IntUnaryOperator postsetIndicator(final Place place) {
        final BitEncodedSet<Transition> postset = place.postset();
        return i -> postset.containsIndex(i) ? -1 : 0;
    }

    public static BasicFitnessEvaluation summarizeInto(EnumCounts<ReplayOutcomes> enumCounts) {
        int fitting = enumCounts.getCount(ReplayOutcomes.FITTING);
        int nonFitting = enumCounts.getCount(ReplayOutcomes.NON_FITTING);
        int underfed = enumCounts.getCount(ReplayOutcomes.WENT_NEGATIVE);
        int overfed = enumCounts.getCount(ReplayOutcomes.OVERFED);
        double sum = fitting + nonFitting;
        return BasicFitnessEvaluation.ofCounts(sum, fitting, underfed, overfed, nonFitting);
    }

    public static int[] getCounts() {
        return new int[ReplayOutcomes.values().length];
    }

    public static void updateCounts(int[] counts, boolean wentUnder, boolean wentOver, boolean notZeroAtEnd, int c) {
        if (!notZeroAtEnd && !wentUnder && !wentOver) counts[ReplayOutcomes.FITTING.ordinal()] += c;
        else {
            counts[ReplayOutcomes.NON_FITTING.ordinal()] += c;
            if (notZeroAtEnd) {
                counts[ReplayOutcomes.OVERFED.ordinal()] += c;
                counts[ReplayOutcomes.NOT_ENDING_ON_ZERO.ordinal()] += c;
            }
            if (wentOver) counts[ReplayOutcomes.WENT_ABOVE_ONE.ordinal()] += c;
            if (wentUnder) counts[ReplayOutcomes.WENT_NEGATIVE.ordinal()] += c;
        }
    }

    public static void updateFittingVariantMask(BitMask bm, boolean wentUnder, boolean wentOver, boolean notZeroAtEnd, int idx) {
        if (!notZeroAtEnd && !wentUnder && !wentOver) bm.set(idx);
    }

    public static void updateBitMasks(BitMask[] bms, boolean wentUnder, boolean wentOver, boolean notZeroAtEnd, int idx) {
        if (!notZeroAtEnd && !wentUnder && !wentOver) bms[ReplayOutcomes.FITTING.ordinal()].set(idx);
        else {
            bms[ReplayOutcomes.NON_FITTING.ordinal()].set(idx);
            if (notZeroAtEnd) {
                bms[ReplayOutcomes.OVERFED.ordinal()].set(idx);
                bms[ReplayOutcomes.NOT_ENDING_ON_ZERO.ordinal()].set(idx);
            }
            if (wentOver) bms[ReplayOutcomes.WENT_ABOVE_ONE.ordinal()].set(idx);
            if (wentUnder) bms[ReplayOutcomes.WENT_NEGATIVE.ordinal()].set(idx);
        }
    }

    public static BitMask[] getBitMasks() {
        BitMask[] bms = new BitMask[ReplayOutcomes.values().length];
        for (int i = 0; i < bms.length; i++) {
            bms[i] = new BitMask();
        }
        return bms;
    }

    public static EnumSet<ReplayOutcomes> getReplayOutcomeEnumSet(boolean wentUnder, boolean wentOver, boolean notZeroAtEnd) {
        if (!notZeroAtEnd && !wentUnder && !wentOver) return EnumSet.of(ReplayOutcomes.FITTING);
        else {
            EnumSet<ReplayOutcomes> enumSet = EnumSet.of(ReplayOutcomes.NON_FITTING);
            if (notZeroAtEnd) {
                enumSet.add(ReplayOutcomes.NOT_ENDING_ON_ZERO);
                enumSet.add(ReplayOutcomes.OVERFED);
            }
            if (wentOver) {
                enumSet.add(ReplayOutcomes.WENT_ABOVE_ONE);
            }
            if (wentUnder) enumSet.add(ReplayOutcomes.WENT_NEGATIVE);
            return enumSet;
        }
    }

    public enum ReplayOutcomes {
        FITTING, OVERFED, NON_FITTING, WENT_ABOVE_ONE, WENT_NEGATIVE, NOT_ENDING_ON_ZERO
    }
}
