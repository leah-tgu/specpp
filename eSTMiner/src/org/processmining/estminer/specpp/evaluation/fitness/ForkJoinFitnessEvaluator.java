package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.util.EnumCounts;
import org.processmining.estminer.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.estminer.specpp.datastructures.util.IndexedItem;
import org.processmining.estminer.specpp.datastructures.util.Tuple2;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVector;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

import java.nio.IntBuffer;
import java.util.EnumSet;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RecursiveTask;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ForkJoinFitnessEvaluator extends AbstractFitnessEvaluator {


    public ForkJoinFitnessEvaluator() {
        componentSystemAdapter().provide(EvaluationRequirements.evaluator(Place.class, SimplestFitnessEvaluation.class, this::eval))
                                .provide(EvaluationRequirements.evaluator(JavaTypingUtils.castClass(EvaluationParameterTuple2.class), SimplestFitnessEvaluation.class, this::subsetEval));

    }

    private SimplestFitnessEvaluation subsetEval(EvaluationParameterTuple2<Place, BitMask> tuple) {
        return eval(tuple.getT1(), tuple.getT2());
    }


    public SimplestFitnessEvaluation eval(Place place) {
        return eval(place, getConsideredVariants());
    }

    public SimplestFitnessEvaluation eval(Place place, BitMask consideredVariants) {
        timeStopper.start(TaskDescription.SIMPLEST_EVALUATION);

        IntUnaryOperator presetIndicator = ReplayUtils.presetIndicator(place);
        IntUnaryOperator postsetIndicator = ReplayUtils.postsetIndicator(place);
        MultiEncodedLog encodedLog = getMultiEncodedLog();

        Stream<IndexedItem<Tuple2<IntBuffer, IntBuffer>>> stream = encodedLog.efficientIndexedStream(false);
        if (consideredVariants != null) stream = stream.filter(ip -> consideredVariants.get(ip.getIndex()));
        Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator = stream.map(ip -> new IndexedItem<>(ip.getIndex(), myBufferBasedReplay(ip.getItem()
                                                                                                                                                            .getT1(), presetIndicator, ip.getItem()
                                                                                                                                                                                         .getT2(), postsetIndicator)))
                                                                                          .spliterator();

        IntVector frequencies = encodedLog.getPresetEncodedLog().getVariantFrequencies();
        EnumCounts<ReplayUtils.ReplayOutcomes> like = computeHere(spliterator, frequencies::get);
        SimplestFitnessEvaluation evaluation = ReplayUtils.summarizeInto(like);
        timeStopper.stop(TaskDescription.SIMPLEST_EVALUATION);
        return evaluation;
    }

    public static MyReplayTask<ReplayUtils.ReplayOutcomes> createReplayTask(Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator, IntUnaryOperator variantFrequencyGetter) {
        return new MyReplayTask<>(ReplayUtils.ReplayOutcomes.values().length, spliterator, variantFrequencyGetter);
    }

    public static EnumCounts<ReplayUtils.ReplayOutcomes> computeHere(Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator, IntUnaryOperator variantFrequencyGetter) {
        return createReplayTask(spliterator, variantFrequencyGetter).computeCountsHere();
    }

    public static EnumCounts<ReplayUtils.ReplayOutcomes> computeForkJoinLike(Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator, IntUnaryOperator variantFrequencyGetter) {
        MyReplayTask<ReplayUtils.ReplayOutcomes> task = createReplayTask(spliterator, variantFrequencyGetter);
        task.fork();
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static EnumSet<ReplayUtils.ReplayOutcomes> myReplay(IntStream presetStream, IntStream postsetStream) {
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
        boolean notZeroAtEnd = acc > 0;
        if (!notZeroAtEnd && !wentUnder && !wentOver) return EnumSet.of(ReplayUtils.ReplayOutcomes.FITTING);
        else {
            EnumSet<ReplayUtils.ReplayOutcomes> enumSet = EnumSet.of(ReplayUtils.ReplayOutcomes.NON_FITTING);
            if (notZeroAtEnd) {
                enumSet.add(ReplayUtils.ReplayOutcomes.NOT_ENDING_ON_ZERO);
                enumSet.add(ReplayUtils.ReplayOutcomes.OVERFED);
            }
            if (wentOver) {
                enumSet.add(ReplayUtils.ReplayOutcomes.WENT_ABOVE_ONE);
            }
            if (wentUnder) enumSet.add(ReplayUtils.ReplayOutcomes.WENT_NEGATIVE);
            return enumSet;
        }
    }

    private static EnumSet<ReplayUtils.ReplayOutcomes> myBufferBasedReplay(IntBuffer presetVariantBuffer, IntUnaryOperator presetIndicator, IntBuffer postsetVariantBuffer, IntUnaryOperator postsetIndicator) {
        int acc = 0;
        boolean wentUnder = false, wentOver = false;
        while (presetVariantBuffer.hasRemaining() && postsetVariantBuffer.hasRemaining()) {
            int postsetExecution = postsetIndicator.applyAsInt(postsetVariantBuffer.get());
            int presetExecution = presetIndicator.applyAsInt(presetVariantBuffer.get());
            acc += postsetExecution;
            wentUnder |= acc < 0;
            acc += presetExecution;
            wentOver |= acc > 1;
        }
        EnumSet<ReplayUtils.ReplayOutcomes> enumSet = EnumSet.noneOf(ReplayUtils.ReplayOutcomes.class);
        if (wentUnder) enumSet.add(ReplayUtils.ReplayOutcomes.WENT_NEGATIVE);
        if (wentOver) {
            enumSet.add(ReplayUtils.ReplayOutcomes.WENT_ABOVE_ONE);
            enumSet.add(ReplayUtils.ReplayOutcomes.OVERFED);
        }
        if (acc > 0) {
            enumSet.add(ReplayUtils.ReplayOutcomes.NOT_ENDING_ON_ZERO);
            enumSet.add(ReplayUtils.ReplayOutcomes.OVERFED);
        }
        if (acc == 0 && !wentUnder && !wentOver) enumSet.add(ReplayUtils.ReplayOutcomes.FITTING);
        else enumSet.add(ReplayUtils.ReplayOutcomes.NON_FITTING);
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
