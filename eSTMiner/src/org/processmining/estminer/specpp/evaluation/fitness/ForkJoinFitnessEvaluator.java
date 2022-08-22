package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.util.IndexedItem;
import org.processmining.estminer.specpp.datastructures.util.Tuple2;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;

import java.nio.IntBuffer;
import java.util.EnumSet;
import java.util.Spliterator;
import java.util.concurrent.ExecutionException;
import java.util.function.IntUnaryOperator;
import java.util.stream.Stream;

public class ForkJoinFitnessEvaluator extends AbstractBasicFitnessEvaluator {


    @Override
    public BasicFitnessEvaluation basicComputation(Place place, BitMask consideredVariants) {
        timeStopper.start(TaskDescription.BASIC_EVALUATION);
        Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator = prepareSpliterator(place, consideredVariants);
        AbstractReplayTask<ReplayUtils.ReplayOutcomes, BasicFitnessEvaluation> simpleReplayTask = createSimpleReplayTask(spliterator);
        BasicFitnessEvaluation result = computeHere(simpleReplayTask);
        timeStopper.stop(TaskDescription.BASIC_EVALUATION);
        return result;
    }

    @Override
    public DetailedFitnessEvaluation detailedComputation(Place place, BitMask consideredVariants) {
        timeStopper.start(TaskDescription.DETAILED_EVALUATION);
        Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator = prepareSpliterator(place, consideredVariants);
        AbstractReplayTask<ReplayUtils.ReplayOutcomes, DetailedFitnessEvaluation> simpleReplayTask = createDetailedReplayTask(spliterator);
        DetailedFitnessEvaluation result = computeHere(simpleReplayTask);
        timeStopper.stop(TaskDescription.DETAILED_EVALUATION);
        return result;
    }

    private Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> prepareSpliterator(Place place, BitMask consideredVariants) {
        IntUnaryOperator presetIndicator = ReplayUtils.presetIndicator(place);
        IntUnaryOperator postsetIndicator = ReplayUtils.postsetIndicator(place);

        Stream<IndexedItem<Tuple2<IntBuffer, IntBuffer>>> stream = getIndexedItemStream();
        if (consideredVariants != null) stream = stream.filter(ip -> consideredVariants.get(ip.getIndex()));
        return stream.map(ip -> new IndexedItem<>(ip.getIndex(), variantReplay(ip.getItem()
                                                                                 .getT1(), presetIndicator, ip.getItem()
                                                                                                              .getT2(), postsetIndicator)))
                     .spliterator();
    }


    public AbstractReplayTask<ReplayUtils.ReplayOutcomes, BasicFitnessEvaluation> createSimpleReplayTask(Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator) {
        return new SimpleReplayTask(spliterator, getVariantFrequencies()::get, ReplayUtils.ReplayOutcomes.values().length);
    }

    public AbstractReplayTask<ReplayUtils.ReplayOutcomes, DetailedFitnessEvaluation> createDetailedReplayTask(Spliterator<IndexedItem<EnumSet<ReplayUtils.ReplayOutcomes>>> spliterator) {
        return new DetailedReplayTask(spliterator, getVariantFrequencies()::get, ReplayUtils.ReplayOutcomes.values().length);
    }

    public static <R> R computeHere(AbstractReplayTask<ReplayUtils.ReplayOutcomes, R> task) {
        return task.computeHere();
    }

    public static <R> R computeForkJoinLike(AbstractReplayTask<ReplayUtils.ReplayOutcomes, R> task) {
        task.fork();
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static EnumSet<ReplayUtils.ReplayOutcomes> variantReplay(IntBuffer presetVariantBuffer, IntUnaryOperator presetIndicator, IntBuffer postsetVariantBuffer, IntUnaryOperator postsetIndicator) {
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
        boolean notEndingOnZero = acc > 0;
        return ReplayUtils.getReplayOutcomeEnumSet(wentUnder, wentOver, notEndingOnZero);
    }


}
