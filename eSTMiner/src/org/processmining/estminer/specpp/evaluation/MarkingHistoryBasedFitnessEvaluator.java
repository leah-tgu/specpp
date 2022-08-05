package org.processmining.estminer.specpp.evaluation;

import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.AbstractComponentSystemUser;
import org.processmining.estminer.specpp.componenting.traits.IsGlobalProvider;
import org.processmining.estminer.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.estminer.specpp.representations.BitMask;
import org.processmining.estminer.specpp.representations.log.impls.DenseVariantMarkingHistories;
import org.processmining.estminer.specpp.representations.petri.Place;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;
import org.processmining.estminer.specpp.util.datastructures.IndexedItem;

import java.util.Spliterator;

public class MarkingHistoryBasedFitnessEvaluator extends AbstractComponentSystemUser implements ProvidesEvaluators, IsGlobalProvider {

    private final DelegatingEvaluator<Place, DenseVariantMarkingHistories> historyMaker = EvaluationRequirements.PLACE_MARKING_HISTORY.emptyDelegator();

    private final DelegatingDataSource<BitMask> variantSubsetSource = DataRequirements.CONSIDERED_VARIANTS.emptyDelegator();

    private final TimeStopper timeStopper = new TimeStopper();

    private BitMask consideredVariants;

    public MarkingHistoryBasedFitnessEvaluator() {
        componentSystemAdapter().require(EvaluationRequirements.PLACE_MARKING_HISTORY, historyMaker)
                                .require(DataRequirements.CONSIDERED_VARIANTS, variantSubsetSource)
                                .provide(EvaluationRequirements.evaluator(Place.class, AggregatedBasicFitnessEvaluation.class, this::aggregatedEval))
                                .provide(EvaluationRequirements.evaluator(Place.class, FullBasicFitnessEvaluation.class, this::fullEval))
                                .provide(SupervisionRequirements.observable("evaluator.performance", PerformanceEvent.class, timeStopper));
    }

    private void updateConsideredVariants() {
        setConsideredVariants(variantSubsetSource.getData());
    }

    public AggregatedBasicFitnessEvaluation aggregatedEval(Place place) {
        timeStopper.start(TaskDescription.AGGREGATED_EVAL);
        updateConsideredVariants();
        DenseVariantMarkingHistories h = historyMaker.eval(place);
        Spliterator<BasicVariantFitnessStatus> among = h.basicFitnessComputationAmong(consideredVariants);
        AggregatedBasicFitnessEvaluation aggregatedBasicFitnessEvaluation = ForkJoinUtils.computeAggregationForkJoinLike(among);
        timeStopper.stop(TaskDescription.AGGREGATED_EVAL);
        return aggregatedBasicFitnessEvaluation;
    }

    public FullBasicFitnessEvaluation fullEval(Place place) {
        timeStopper.start(TaskDescription.FULL_EVAL);
        updateConsideredVariants();
        DenseVariantMarkingHistories h = historyMaker.eval(place);
        Spliterator<IndexedItem<BasicVariantFitnessStatus>> among = h.basicIndexedFitnessComputationAmong(consideredVariants);
        FullBasicFitnessEvaluation fullBasicFitnessEvaluation = ForkJoinUtils.computeFullSummaryForkJoinLike(among);
        timeStopper.stop(TaskDescription.FULL_EVAL);
        return fullBasicFitnessEvaluation;
    }

    @Override
    public String toString() {
        return "FitnessEvaluator(" + historyMaker.getClass().getSimpleName() + ", " + variantSubsetSource + ")";
    }

    public BitMask getConsideredVariants() {
        return consideredVariants;
    }

    public void setConsideredVariants(BitMask consideredVariants) {
        this.consideredVariants = consideredVariants;
    }

}
