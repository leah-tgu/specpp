package org.processmining.estminer.specpp.composition;

import org.processmining.estminer.specpp.base.ExaminingComposition;
import org.processmining.estminer.specpp.base.impls.ArrayListComposition;
import org.processmining.estminer.specpp.base.impls.LightweightPlaceCollection;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.estminer.specpp.datastructures.log.impls.DenseVariantMarkingHistories;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.evaluation.implicitness.ImplicitnessRating;
import org.processmining.estminer.specpp.evaluation.implicitness.ReplayBasedImplicitnessCalculator;

import java.util.HashMap;
import java.util.Map;

public class PlaceCollection extends LightweightPlaceCollection implements ExaminingComposition<Place>, ProvidesEvaluators {
    private final DelegatingEvaluator<Place, DenseVariantMarkingHistories> historyMaker = EvaluationRequirements.PLACE_MARKING_HISTORY.emptyDelegator();
    private final Map<Place, DenseVariantMarkingHistories> histories;

    private final ComponentSystemAdapter componentSystemAdapter = new ComponentSystemAdapter();

    public PlaceCollection() {
        histories = new HashMap<>();
        componentSystemAdapter().require(EvaluationRequirements.PLACE_MARKING_HISTORY, historyMaker)
                                .provide(EvaluationRequirements.evaluator(EvaluationRequirements.PLACE_IMPLICITNESS, this::rateImplicitness));
    }

    @Override
    public void accept(Place place) {
        super.accept(place);
        histories.put(place, historyMaker.eval(place));
    }

    public ImplicitnessRating rateImplicitness(Place place) {
        return ReplayBasedImplicitnessCalculator.replaySubregionImplicitness(place, historyMaker.eval(place), histories);
    }

    @Override
    public void remove(Place candidate) {
        super.remove(candidate);
        histories.remove(candidate);
    }

    @Override
    public ComponentSystemAdapter componentSystemAdapter() {
        return componentSystemAdapter;
    }

}
