package org.processmining.estminer.specpp.composition;

import org.processmining.estminer.specpp.base.Evaluator;
import org.processmining.estminer.specpp.base.ExaminingComposition;
import org.processmining.estminer.specpp.base.impls.LightweightPlaceCollection;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.estminer.specpp.datastructures.log.impls.DenseVariantMarkingHistories;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.util.ComputingCache;
import org.processmining.estminer.specpp.evaluation.implicitness.ImplicitnessRating;
import org.processmining.estminer.specpp.evaluation.implicitness.ReplayBasedImplicitnessCalculator;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;

import java.util.HashMap;
import java.util.Map;

public class PlaceCollection extends LightweightPlaceCollection implements ExaminingComposition<Place> {
    private final Evaluator<Place, DenseVariantMarkingHistories> historyMaker;
    private final Map<Place, DenseVariantMarkingHistories> histories;

    private final ComponentSystemAdapter componentSystemAdapter = new ComponentSystemAdapter();
    private final TimeStopper timeStopper = new TimeStopper();

    public PlaceCollection() {
        histories = new HashMap<>();
        DelegatingEvaluator<Place, DenseVariantMarkingHistories> pureEvaluator = new DelegatingEvaluator<>();
        componentSystemAdapter().require(EvaluationRequirements.PLACE_MARKING_HISTORY, pureEvaluator)
                                .provide(EvaluationRequirements.evaluator(EvaluationRequirements.PLACE_IMPLICITNESS, this::rateImplicitness))
                                .provide(SupervisionRequirements.observable("concurrent_implicitness.performance", PerformanceEvent.class, timeStopper));
        ComputingCache<Place, DenseVariantMarkingHistories> cache = new ComputingCache<>(100, pureEvaluator);
        historyMaker = cache::get;
    }

    @Override
    public void accept(Place place) {
        super.accept(place);
        histories.put(place, historyMaker.eval(place));
    }

    public ImplicitnessRating rateImplicitness(Place place) {
        timeStopper.start(TaskDescription.REPLAY_BASED_CONCURRENT_IMPLICITNESS);
        ImplicitnessRating implicitnessRating = ReplayBasedImplicitnessCalculator.replaySubregionImplicitness(place, historyMaker.eval(place), histories);
        timeStopper.stop(TaskDescription.REPLAY_BASED_CONCURRENT_IMPLICITNESS);
        return implicitnessRating;
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
