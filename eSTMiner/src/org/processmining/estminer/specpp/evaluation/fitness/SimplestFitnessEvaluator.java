package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.datastructures.log.Log;
import org.processmining.estminer.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.util.EnumCounts;
import org.processmining.estminer.specpp.datastructures.util.IndexedItem;
import org.processmining.estminer.specpp.datastructures.util.Tuple2;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVector;
import org.processmining.estminer.specpp.evaluation.markings.QuickReplay;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.supervision.supervisors.DebuggingSupervisor;

import java.util.EnumSet;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SimplestFitnessEvaluator extends AbstractFitnessEvaluator {

    private final DelegatingDataSource<Log> logSource = new DelegatingDataSource<>();

    public SimplestFitnessEvaluator() {
        componentSystemAdapter().require(DataRequirements.RAW_LOG, logSource)
                                .provide(EvaluationRequirements.evaluator(Place.class, SimplestFitnessEvaluation.class, this::eval));
    }

    public SimplestFitnessEvaluation eval(Place place) {
        timeStopper.start(TaskDescription.SIMPLEST_EVALUATION);

        IntUnaryOperator presetIndicator = QuickReplay.presetIndicator(place);
        IntUnaryOperator postsetIndicator = QuickReplay.postsetIndicator(place);
        MultiEncodedLog encodedLog = getMultiEncodedLog();

        IntVector frequencies = encodedLog.getPresetEncodedLog().getVariantFrequencies();
        Stream<IndexedItem<Tuple2<IntStream, IntStream>>> stream = encodedLog.efficientIndexedStream(false);

        Spliterator<IndexedItem<EnumSet<ReplayOutcomes>>> spliterator = stream.map(ip -> new IndexedItem<>(ip.getIndex(), myReplay(ip.getItem()
                                                                                                                                     .getT1()
                                                                                                                                     .map(presetIndicator), ip.getItem()
                                                                                                                                                              .getT2()
                                                                                                                                                              .map(postsetIndicator))))
                                                                              .spliterator();

        EnumCounts<ReplayOutcomes> like = computeForkJoinLike(spliterator, frequencies::get);
        // TODO probably incorrect logic here regarding normalization over all traces
        SimplestFitnessEvaluation evaluation = summarizeInto(like);
        timeStopper.stop(TaskDescription.SIMPLEST_EVALUATION);
        return evaluation;
    }

    public SimplestFitnessEvaluation summarizeInto(EnumCounts<ReplayOutcomes> enumCounts) {
        int fitting = enumCounts.getCount(ReplayOutcomes.FITTING);
        int nonFitting = enumCounts.getCount(ReplayOutcomes.NON_FITTING);
        int underfed = enumCounts.getCount(ReplayOutcomes.EVER_NEGATIVE);
        int overfed = enumCounts.getCount(ReplayOutcomes.OVERFED);
        double sum = fitting + nonFitting;
        return new SimplestFitnessEvaluation(fitting / sum, underfed / sum, overfed / sum, nonFitting / sum);
    }

    public static MyReplayTask<ReplayOutcomes> createReplayTask(Spliterator<IndexedItem<EnumSet<ReplayOutcomes>>> spliterator, IntUnaryOperator variantFrequencyGetter) {
        return new MyReplayTask<>(ReplayOutcomes.values().length, spliterator, variantFrequencyGetter);
    }

    public static EnumCounts<ReplayOutcomes> computeHere(Spliterator<IndexedItem<EnumSet<ReplayOutcomes>>> spliterator, IntUnaryOperator variantFrequencyGetter) {
        return createReplayTask(spliterator, variantFrequencyGetter).computeCountsHere();
    }

    public static EnumCounts<ReplayOutcomes> computeForkJoinLike(Spliterator<IndexedItem<EnumSet<ReplayOutcomes>>> spliterator, IntUnaryOperator variantFrequencyGetter) {
        MyReplayTask<ReplayOutcomes> task = createReplayTask(spliterator, variantFrequencyGetter);
        task.fork();
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public enum ReplayOutcomes {
        FITTING, OVERFED, NON_FITTING, EVER_ABOVE_ONE, EVER_NEGATIVE, NOT_ENDING_ON_ZERO
    }

    private static EnumSet<ReplayOutcomes> myReplay(IntStream presetStream, IntStream postsetStream) {
        PrimitiveIterator.OfInt postIt = postsetStream.iterator(), preIt = presetStream.iterator();
        int acc = 0;
        boolean wentUnder = false, wentOver = false;
        while (postIt.hasNext() && preIt.hasNext()) {
            int postsetExecution = postIt.nextInt(), presetExecution = preIt.nextInt();
            acc += postsetExecution;
            wentUnder |= acc < 0;
            acc += presetExecution;
            wentOver |= acc > 1;
        }
        EnumSet<ReplayOutcomes> enumSet = EnumSet.noneOf(ReplayOutcomes.class);
        if (wentUnder) enumSet.add(ReplayOutcomes.EVER_NEGATIVE);
        if (wentOver) {
            enumSet.add(ReplayOutcomes.EVER_ABOVE_ONE);
            enumSet.add(ReplayOutcomes.OVERFED);
        }
        if (acc > 0) {
            enumSet.add(ReplayOutcomes.NOT_ENDING_ON_ZERO);
            enumSet.add(ReplayOutcomes.OVERFED);
        }
        if (acc == 0 && !wentUnder && !wentOver) enumSet.add(ReplayOutcomes.FITTING);
        else enumSet.add(ReplayOutcomes.NON_FITTING);
        return enumSet;
    }


    public static class MyReplayTask<E extends Enum<E>> extends RecursiveTask<EnumCounts<E>> {
        protected static final long MIN_SPLITTING_SIZE = 100;
        private final Spliterator<IndexedItem<EnumSet<E>>> toAggregate;
        private final IntUnaryOperator variantCountMapper;
        private final int enumLength;

        public MyReplayTask(int enumLength, Spliterator<IndexedItem<EnumSet<E>>> toAggregate, IntUnaryOperator variantCountMapper) {
            this.enumLength = enumLength;
            this.toAggregate = toAggregate;
            this.variantCountMapper = variantCountMapper;
        }

        protected EnumCounts<E> computeCountsHere() {
            int[] counts = new int[enumLength];
            toAggregate.forEachRemaining(ii -> {
                for (E e : ii.getItem()) {
                    counts[e.ordinal()] += variantCountMapper.applyAsInt(ii.getIndex());
                }
            });
            return new EnumCounts<>(counts);
        }

        protected EnumCounts<E> combineCountsIntoFirst(EnumCounts<E> first, EnumCounts<E> second) {
            for (int i = 0; i < first.counts.length; i++) {
                first.counts[i] += second.counts[i];
            }
            return first;
        }

        protected MyReplayTask<E> createSubTask(int enumLength, Spliterator<IndexedItem<EnumSet<E>>> spliterator) {
            return new MyReplayTask<>(enumLength, spliterator, variantCountMapper);
        }

        @Override
        protected EnumCounts<E> compute() {
            if (toAggregate.getExactSizeIfKnown() < MIN_SPLITTING_SIZE) {
                return computeCountsHere();
            } else {
                Spliterator<IndexedItem<EnumSet<E>>> split = toAggregate.trySplit();
                if (split == null) return computeCountsHere();
                else {
                    MyReplayTask<E> taskA = createSubTask(enumLength, toAggregate);
                    MyReplayTask<E> taskB = createSubTask(enumLength, split);
                    taskB.fork();
                    return combineCountsIntoFirst(taskA.compute(), taskB.join());
                }
            }
        }
    }

}
