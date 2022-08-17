package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.Transition;
import org.processmining.estminer.specpp.datastructures.util.EnumCounts;

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

    public static SimplestFitnessEvaluation summarizeInto(EnumCounts<ReplayOutcomes> enumCounts) {
        int fitting = enumCounts.getCount(ReplayOutcomes.FITTING);
        int nonFitting = enumCounts.getCount(ReplayOutcomes.NON_FITTING);
        int underfed = enumCounts.getCount(ReplayOutcomes.WENT_NEGATIVE);
        int overfed = enumCounts.getCount(ReplayOutcomes.OVERFED);
        double sum = fitting + nonFitting;
        return new SimplestFitnessEvaluation(fitting / sum, underfed / sum, overfed / sum, nonFitting / sum);
    }

    public enum ReplayOutcomes {
        FITTING, OVERFED, NON_FITTING, WENT_ABOVE_ONE, WENT_NEGATIVE, NOT_ENDING_ON_ZERO
    }
}
