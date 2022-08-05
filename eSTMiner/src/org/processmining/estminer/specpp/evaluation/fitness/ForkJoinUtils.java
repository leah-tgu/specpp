package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.util.EnumBitMasks;
import org.processmining.estminer.specpp.datastructures.util.EnumCounts;
import org.processmining.estminer.specpp.datastructures.util.IndexedItem;

import java.util.Arrays;
import java.util.Spliterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;

public class ForkJoinUtils {

    public static AggregatedBasicFitnessEvaluation computeAggregationHere(Spliterator<BasicVariantFitnessStatus> spliterator) {
        int enumLength = BasicVariantFitnessStatus.values().length;
        int[] counts = new int[enumLength];
        spliterator.forEachRemaining(rr -> counts[rr.ordinal()]++);
        return countsToEvaluation(new EnumCounts<>(counts));
    }

    public static FullBasicFitnessEvaluation computeFullSummaryHere(Spliterator<IndexedItem<BasicVariantFitnessStatus>> spliterator) {
        int enumLength = BasicVariantFitnessStatus.values().length;
        BitMask[] bitMasks = createBitMaskArray(enumLength);
        spliterator.forEachRemaining(ii -> bitMasks[ii.getItem().ordinal()].set(ii.getIndex()));
        return bitMasksToEvaluation(new EnumBitMasks<>(bitMasks));
    }

    public static AggregatedBasicFitnessEvaluation computeAggregationForkJoinLike(Spliterator<BasicVariantFitnessStatus> spliterator) {
        int enumLength = BasicVariantFitnessStatus.values().length;
        EnumCountingTask<BasicVariantFitnessStatus> countingTask = new EnumCountingTask<>(enumLength, spliterator);
        countingTask.fork();
        try {
            EnumCounts<BasicVariantFitnessStatus> counts = countingTask.get();
            return countsToEvaluation(counts);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static AggregatedBasicFitnessEvaluation countsToEvaluation(EnumCounts<BasicVariantFitnessStatus> enumCounts) {
        double total = Arrays.stream(enumCounts.counts).sum();
        int length = enumCounts.counts.length;
        double[] fractions = new double[length];
        for (int i = 0; i < length; i++) {
            fractions[i] = enumCounts.counts[i] / total;
        }
        return new AggregatedBasicFitnessEvaluation(fractions);
    }

    public static FullBasicFitnessEvaluation computeFullSummaryForkJoinLike(Spliterator<IndexedItem<BasicVariantFitnessStatus>> spliterator) {
        int enumLength = BasicVariantFitnessStatus.values().length;
        IndexedEnumCountingTask<BasicVariantFitnessStatus> countingTask = new IndexedEnumCountingTask<>(enumLength, spliterator);
        countingTask.fork();
        try {
            EnumBitMasks<BasicVariantFitnessStatus> bitMasks = countingTask.get();
            return bitMasksToEvaluation(bitMasks);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static FullBasicFitnessEvaluation bitMasksToEvaluation(EnumBitMasks<BasicVariantFitnessStatus> bitMasks) {
        return new FullBasicFitnessEvaluation(bitMasks);
    }

    public static BitMask[] createBitMaskArray(int enumLength) {
        BitMask[] masks = new BitMask[enumLength];
        for (int i = 0; i < masks.length; i++) {
            masks[i] = new BitMask();
        }
        return masks;
    }

    public static abstract class AbstractRecursiveEnumTask<E extends Enum<E>, I, R> extends RecursiveTask<R> {
        protected static final long MIN_SPLITTING_SIZE = 100;
        protected final int enumLength;
        private final Spliterator<I> toAggregate;

        public AbstractRecursiveEnumTask(int enumLength, Spliterator<I> toAggregate) {
            this.enumLength = enumLength;
            this.toAggregate = toAggregate;
        }

        protected abstract R computeHere(Spliterator<I> spliterator);

        protected abstract R combineIntoFirst(R first, R second);

        protected abstract AbstractRecursiveEnumTask<E, I, R> createSubTask(int enumLength, Spliterator<I> toAggregate);

        @Override
        protected R compute() {
            if (toAggregate.getExactSizeIfKnown() < MIN_SPLITTING_SIZE) {
                return computeHere(toAggregate);
            } else {
                Spliterator<I> split = toAggregate.trySplit();
                if (split == null) return computeHere(toAggregate);
                else {
                    AbstractRecursiveEnumTask<E, I, R> taskA = createSubTask(enumLength, toAggregate);
                    AbstractRecursiveEnumTask<E, I, R> taskB = createSubTask(enumLength, split);
                    taskB.fork();
                    return combineIntoFirst(taskA.compute(), taskB.join());
                }
            }
        }
    }

    public static class IndexedEnumCountingTask<E extends Enum<E>> extends AbstractRecursiveEnumTask<E, IndexedItem<E>, EnumBitMasks<E>> {

        public IndexedEnumCountingTask(int enumLength, Spliterator<IndexedItem<E>> toAggregate) {
            super(enumLength, toAggregate);
        }

        @Override
        protected EnumBitMasks<E> computeHere(Spliterator<IndexedItem<E>> spliterator) {
            BitMask[] masks = createBitMaskArray(enumLength);
            spliterator.forEachRemaining(ii -> masks[ii.getItem().ordinal()].set(ii.getIndex()));
            return new EnumBitMasks<>(masks);
        }

        @Override
        protected EnumBitMasks<E> combineIntoFirst(EnumBitMasks<E> first, EnumBitMasks<E> second) {
            for (int i = 0; i < first.bitMasks.length; i++) {
                first.bitMasks[i].union(second.bitMasks[i]);
            }
            return first;
        }

        @Override
        protected AbstractRecursiveEnumTask<E, IndexedItem<E>, EnumBitMasks<E>> createSubTask(int enumLength, Spliterator<IndexedItem<E>> toAggregate) {
            return new IndexedEnumCountingTask<>(enumLength, toAggregate);
        }

    }

    public static class EnumCountingTask<E extends Enum<E>> extends AbstractRecursiveEnumTask<E, E, EnumCounts<E>> {


        public EnumCountingTask(int enumLength, Spliterator<E> toAggregate) {
            super(enumLength, toAggregate);
        }

        @Override
        protected EnumCounts<E> computeHere(Spliterator<E> spliterator) {
            int[] counts = new int[enumLength];
            spliterator.forEachRemaining(rr -> counts[rr.ordinal()]++);
            return new EnumCounts<>(counts);
        }

        @Override
        protected EnumCounts<E> combineIntoFirst(EnumCounts<E> first, EnumCounts<E> second) {
            for (int i = 0; i < first.counts.length; i++) {
                first.counts[i] += second.counts[i];
            }
            return first;
        }


        @Override
        protected AbstractRecursiveEnumTask<E, E, EnumCounts<E>> createSubTask(int enumLength, Spliterator<E> toAggregate) {
            return new EnumCountingTask<>(enumLength, toAggregate);
        }
    }
}
