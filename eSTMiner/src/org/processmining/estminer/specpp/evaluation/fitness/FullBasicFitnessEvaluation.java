package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.base.CandidateEvaluation;
import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.util.EnumBitMasks;
import org.processmining.estminer.specpp.datastructures.util.EnumFractions;

public class FullBasicFitnessEvaluation implements CandidateEvaluation {

    private final EnumBitMasks<BasicVariantFitnessStatus> bitMasks;
    private final EnumFractions<BasicVariantFitnessStatus> fractions;

    public FullBasicFitnessEvaluation(EnumBitMasks<BasicVariantFitnessStatus> bitMasks) {
        this.bitMasks = bitMasks;
        int fittingCount = bitMasks.getBitMask(BasicVariantFitnessStatus.FITTING).cardinality();
        int underfedCount = bitMasks.getBitMask(BasicVariantFitnessStatus.GOES_NEGATIVE).cardinality();
        int overfedCount = bitMasks.getBitMask(BasicVariantFitnessStatus.NON_SAFE).cardinality();
        int noEndingOnZeroCount = bitMasks.getBitMask(BasicVariantFitnessStatus.NOT_ENDING_ON_ZERO).cardinality();
        double sum = fittingCount + underfedCount + overfedCount + noEndingOnZeroCount;
        this.fractions = new EnumFractions<>(new double[]{fittingCount / sum, underfedCount / sum, overfedCount / sum, noEndingOnZeroCount / sum});
    }

    public FullBasicFitnessEvaluation(int consideredVariantCount, BitMask replayableVariants, BitMask underfedVariants, BitMask overfedVariants, BitMask notEndingOnZeroVariants) {
        bitMasks = new EnumBitMasks<>(new BitMask[]{replayableVariants, underfedVariants, overfedVariants, notEndingOnZeroVariants});
        fractions = new EnumFractions<>(new double[]{replayableVariants.cardinality() / (double) consideredVariantCount, underfedVariants.cardinality() / (double) consideredVariantCount, overfedVariants.cardinality() / (double) consideredVariantCount, notEndingOnZeroVariants.cardinality() / (double) consideredVariantCount});
    }

    public double getReplayableVariantFraction() {
        return fractions.getFraction(BasicVariantFitnessStatus.FITTING);
    }

    public double getUnderfedVariantFraction() {
        return fractions.getFraction(BasicVariantFitnessStatus.GOES_NEGATIVE);
    }

    public double getOverfedVariantFraction() {
        return fractions.getFraction(BasicVariantFitnessStatus.NON_SAFE);
    }

    public double getNotEndingOnZeroFraction() {
        return fractions.getFraction(BasicVariantFitnessStatus.NOT_ENDING_ON_ZERO);
    }

    public BitMask getReplayableVariants() {
        return bitMasks.getBitMask(BasicVariantFitnessStatus.FITTING);
    }

    public BitMask getUnderfedVariants() {
        return bitMasks.getBitMask(BasicVariantFitnessStatus.GOES_NEGATIVE);
    }

    public BitMask getOverfedVariants() {
        return bitMasks.getBitMask(BasicVariantFitnessStatus.NON_SAFE);
    }

    public BitMask getNotEndingOnZeroVariants() {
        return bitMasks.getBitMask(BasicVariantFitnessStatus.NOT_ENDING_ON_ZERO);
    }

    @Override
    public String toString() {
        return "FullBasicFitnessEvaluation(" + bitMasks + ", " + fractions + ")";
    }

}
