package org.processmining.estminer.specpp.evaluation;

import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.AbstractComponentSystemUser;
import org.processmining.estminer.specpp.componenting.traits.IsGlobalProvider;
import org.processmining.estminer.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.estminer.specpp.representations.BitMask;
import org.processmining.estminer.specpp.representations.log.QuickReplay;
import org.processmining.estminer.specpp.representations.log.impls.MultiEncodedLog;
import org.processmining.estminer.specpp.representations.petri.Place;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;
import org.processmining.estminer.specpp.util.datastructures.IndexedItem;
import org.processmining.estminer.specpp.util.datastructures.Pair;

import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ShortCircuitingFitnessEvaluator extends AbstractComponentSystemUser implements ProvidesEvaluators, IsGlobalProvider {

    private final DelegatingDataSource<MultiEncodedLog> multiEncodedLogSource = DataRequirements.ENC_LOG.emptyDelegator();
    private final DelegatingDataSource<BitMask> variantSubsetSource = DataRequirements.CONSIDERED_VARIANTS.emptyDelegator();

    private final TimeStopper timeStopper = new TimeStopper();
    private BitMask consideredVariants;

    public ShortCircuitingFitnessEvaluator() {
        componentSystemAdapter().require(DataRequirements.CONSIDERED_VARIANTS, variantSubsetSource)
                                .require(DataRequirements.ENC_LOG, multiEncodedLogSource)
                                .provide(EvaluationRequirements.evaluator(Place.class, AggregatedBasicFitnessEvaluation.class, this::aggregatedEval))
                                .provide(EvaluationRequirements.evaluator(Place.class, FullBasicFitnessEvaluation.class, this::fullEval))
                                .provide(SupervisionRequirements.observable("evaluator.performance", PerformanceEvent.class, timeStopper));
    }

    public static AggregatedBasicFitnessEvaluation aggregatedReplay(MultiEncodedLog multiEncodedLog, Place place) {
        return aggregatedShortCircuitingReplay(multiEncodedLog.stream(), place);
    }

    public static AggregatedBasicFitnessEvaluation aggregatedReplayOn(BitMask variantMask, MultiEncodedLog multiEncodedLog, Place place) {
        return aggregatedShortCircuitingReplay(multiEncodedLog.stream()
                                                              .filter(ii -> variantMask.get(ii.getIndex())), place);
    }

    public static AggregatedBasicFitnessEvaluation aggregatedShortCircuitingReplay(Stream<IndexedItem<Pair<IntStream>>> logStream, Place place) {
        IntUnaryOperator presetIndicator = QuickReplay.presetIndicator(place);
        IntUnaryOperator postsetIndicator = QuickReplay.postsetIndicator(place);
        Spliterator<BasicVariantFitnessStatus> spliterator = logStream.map(ii -> shortCircuitingReplay(ii.getItem()
                                                                                                         .second()
                                                                                                         .map(postsetIndicator), ii.getItem()
                                                                                                                                   .first()
                                                                                                                                   .map(presetIndicator)))
                                                                      .spliterator();

        return ForkJoinUtils.computeAggregationForkJoinLike(spliterator);
    }

    public static FullBasicFitnessEvaluation fullReplay(MultiEncodedLog multiEncodedLog, Place place) {
        return fullShortCircuitingReplay(multiEncodedLog.stream(), place);
    }

    public static FullBasicFitnessEvaluation fullReplayOn(BitMask variantMask, MultiEncodedLog multiEncodedLog, Place place) {
        return fullShortCircuitingReplay(multiEncodedLog.stream().filter(ii -> variantMask.get(ii.getIndex())), place);
    }

    public static FullBasicFitnessEvaluation fullShortCircuitingReplay(Stream<IndexedItem<Pair<IntStream>>> logStream, Place place) {
        IntUnaryOperator presetIndicator = QuickReplay.presetIndicator(place);
        IntUnaryOperator postsetIndicator = QuickReplay.postsetIndicator(place);
        Spliterator<IndexedItem<BasicVariantFitnessStatus>> spliterator = logStream.map(ii -> ii.map(p -> shortCircuitingReplay(p.second()
                                                                                                                                 .map(postsetIndicator), p.first()
                                                                                                                                                          .map(presetIndicator))))
                                                                                   .spliterator();
        return ForkJoinUtils.computeFullSummaryForkJoinLike(spliterator);
    }

    private static BasicVariantFitnessStatus shortCircuitingReplay(IntStream postsetStream, IntStream presetStream) {
        PrimitiveIterator.OfInt postIt = postsetStream.iterator(), preIt = presetStream.iterator();
        int acc = 0;
        while (postIt.hasNext() && preIt.hasNext()) {
            int postsetExecution = postIt.nextInt(), presetExecution = preIt.nextInt();
            acc += postsetExecution;
            if (acc < 0) return BasicVariantFitnessStatus.UNDERFED;
            acc += presetExecution;
            if (acc > 1) return BasicVariantFitnessStatus.OVERFED;
        }
        return acc == 0 ? BasicVariantFitnessStatus.FITTING : BasicVariantFitnessStatus.NOT_ENDING_ON_ZERO;
    }

    private void updateConsideredVariants() {
        setConsideredVariants(variantSubsetSource.getData());
    }

    public BitMask getConsideredVariants() {
        return consideredVariants;
    }

    public void setConsideredVariants(BitMask consideredVariants) {
        this.consideredVariants = consideredVariants;
    }

    public AggregatedBasicFitnessEvaluation aggregatedEval(Place input) {
        timeStopper.start(TaskDescription.SHORT_CIRCUITING_AGGREGATED_EVAL);
        updateConsideredVariants();
        AggregatedBasicFitnessEvaluation aggregatedBasicFitnessEvaluation = variantSubsetSource.isSet() ? aggregatedReplayOn(consideredVariants, multiEncodedLogSource.getData(), input) : aggregatedReplay(multiEncodedLogSource.getData(), input);
        timeStopper.stop(TaskDescription.SHORT_CIRCUITING_AGGREGATED_EVAL);
        return aggregatedBasicFitnessEvaluation;
    }


    public FullBasicFitnessEvaluation fullEval(Place input) {
        timeStopper.start(TaskDescription.SHORT_CIRCUITING_FULL_EVAL);
        updateConsideredVariants();
        FullBasicFitnessEvaluation fullBasicFitnessEvaluation = variantSubsetSource.isSet() ? fullReplayOn(consideredVariants, multiEncodedLogSource.getData(), input) : fullReplay(multiEncodedLogSource.getData(), input);
        timeStopper.stop(TaskDescription.SHORT_CIRCUITING_FULL_EVAL);
        return fullBasicFitnessEvaluation;
    }

    @Override
    public String toString() {
        return "ShortCircuitingReplayer(" + variantSubsetSource + ")";
    }

}
