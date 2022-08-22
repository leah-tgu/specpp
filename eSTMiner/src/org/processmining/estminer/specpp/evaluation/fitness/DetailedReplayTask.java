package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.util.EnumCounts;
import org.processmining.estminer.specpp.datastructures.util.IndexedItem;

import java.util.EnumSet;
import java.util.Spliterator;
import java.util.function.IntUnaryOperator;

public class DetailedReplayTask extends AbstractReplayTask<ReplayUtils.ReplayOutcomes, DetailedFitnessEvaluation> {
    public DetailedReplayTask(Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> toAggregate, IntUnaryOperator variantCountMapper, int enumLength) {
        super(toAggregate, variantCountMapper, enumLength);
    }

    @Override
    protected DetailedFitnessEvaluation computeHere() {
        BitMask bm = new BitMask();
        int[] counts = new int[enumLength];
        toAggregate.forEachRemaining(ii -> {
            EnumSet<ReplayUtils.ReplayOutcomes> set = ii.getItem();
            if (set.contains(ReplayUtils.ReplayOutcomes.FITTING)) bm.set(ii.getIndex());
            int c = variantCountMapper.applyAsInt(ii.getIndex());
            for (ReplayUtils.ReplayOutcomes e : ii.getItem()) {
                counts[e.ordinal()] += c;
            }
        });
        BasicFitnessEvaluation basicFitnessEvaluation = ReplayUtils.summarizeInto(new EnumCounts<>(counts));
        return new DetailedFitnessEvaluation(bm, basicFitnessEvaluation);
    }

    @Override
    protected DetailedFitnessEvaluation combineIntoFirst(DetailedFitnessEvaluation first, DetailedFitnessEvaluation second) {
        first.disjointMerge(second);
        return first;
    }

    @Override
    protected AbstractReplayTask<ReplayUtils.ReplayOutcomes, DetailedFitnessEvaluation> createSubTask(int enumLength, Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator) {
        return new DetailedReplayTask(spliterator, variantCountMapper, enumLength);
    }

}
