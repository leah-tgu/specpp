package org.processmining.estminer.specpp.postprocessing;

import org.processmining.estminer.specpp.base.Evaluator;
import org.processmining.estminer.specpp.base.PostProcessor;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAwareBuilder;
import org.processmining.estminer.specpp.datastructures.log.impls.DenseVariantMarkingHistories;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.util.ImmutableTuple2;
import org.processmining.estminer.specpp.datastructures.util.Tuple2;
import org.python.google.common.collect.Maps;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ReplayBasedImplicitnessPostProcessing implements PostProcessor<PetriNet, PetriNet> {

    private final Evaluator<Place, DenseVariantMarkingHistories> markingHistoriesEvaluator;

    public ReplayBasedImplicitnessPostProcessing(Evaluator<Place, DenseVariantMarkingHistories> markingHistoriesEvaluator) {
        this.markingHistoriesEvaluator = markingHistoriesEvaluator;
    }


    public static class Builder extends ComponentSystemAwareBuilder<PostProcessor<PetriNet, PetriNet>> {

        private final DelegatingEvaluator<Place, DenseVariantMarkingHistories> evaluatorDelegator = new DelegatingEvaluator<>();

        public Builder() {
            componentSystemAdapter().require(EvaluationRequirements.PLACE_MARKING_HISTORY, evaluatorDelegator);
        }

        @Override
        public ReplayBasedImplicitnessPostProcessing buildIfFullySatisfied() {
            return new ReplayBasedImplicitnessPostProcessing(evaluatorDelegator);
        }
    }

    @Override
    public PetriNet postProcess(PetriNet result) {
        Set<Place> places = new HashSet<>(result.getPlaces());

        Map<Place, DenseVariantMarkingHistories> histories = places.stream()
                                                                   .parallel()
                                                                   .map(p -> new ImmutableTuple2<>(p, markingHistoriesEvaluator.eval(p)))
                                                                   .collect(Collectors.toMap(Tuple2::getT1, Tuple2::getT2));

        Set<Place> exclusionZone = new HashSet<>();
        for (Place place : places) {
            DenseVariantMarkingHistories history = histories.get(place);

            Map<Place, DenseVariantMarkingHistories> otherHistories = Maps.filterKeys(histories, p -> !place.equals(p));

            if (otherHistories.values().stream().parallel().anyMatch(history::gt)) {
                exclusionZone.add(place);
                histories.remove(place);
            }

        }

        places.removeAll(exclusionZone);
        return new PetriNet(places);
    }
}
