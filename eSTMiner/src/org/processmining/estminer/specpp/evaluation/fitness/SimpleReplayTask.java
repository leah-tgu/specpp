package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.datastructures.util.EnumCounts;
import org.processmining.estminer.specpp.datastructures.util.IndexedItem;

import java.util.EnumSet;
import java.util.Spliterator;
import java.util.function.IntUnaryOperator;

public class SimpleReplayTask extends AbstractReplayTask<ReplayUtils.ReplayOutcomes, BasicFitnessEvaluation> {

    public SimpleReplayTask(Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> toAggregate, IntUnaryOperator variantCountMapper, int enumLength) {
        super(toAggregate, variantCountMapper, enumLength);
    }

    @Override
    protected BasicFitnessEvaluation computeHere() {
        int[] counts = new int[enumLength];
        toAggregate.forEachRemaining(ii -> {
            int c = variantCountMapper.applyAsInt(ii.getIndex());
            for (ReplayUtils.ReplayOutcomes e : ii.getItem()) {
                counts[e.ordinal()] += c;
            }
        });
        return ReplayUtils.summarizeInto(new EnumCounts<>(counts));
    }

    @Override
    protected BasicFitnessEvaluation combineIntoFirst(BasicFitnessEvaluation first, BasicFitnessEvaluation second) {
        first.disjointMerge(second);
        return first;
    }

    @Override
    protected SimpleReplayTask createSubTask(int enumLength, Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator) {
        return new SimpleReplayTask(spliterator, variantCountMapper, enumLength);
    }

}
