package org.processmining.estminer.specpp.composition;

import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.log.impls.DenseVariantMarkingHistories;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.evaluation.implicitness.ImplicitnessRating;
import org.processmining.estminer.specpp.evaluation.implicitness.ReplayBasedImplicitnessCalculator;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;

import java.util.function.Consumer;

public class TestPlaceCollection extends PlaceCollection {
    @Override
    public ImplicitnessRating rateImplicitness(Place place) {
        timeStopper.start(TaskDescription.REPLAY_BASED_CONCURRENT_IMPLICITNESS);
        DenseVariantMarkingHistories h = historyMaker.eval(place);
        BitMask mask = getCurrentlySupportedVariants();
        ImplicitnessRating implicitnessRating = ReplayBasedImplicitnessCalculator.replaySubregionImplicitnessOn(mask, place, h, histories);
        timeStopper.stop(TaskDescription.REPLAY_BASED_CONCURRENT_IMPLICITNESS);
        return implicitnessRating;
    }
}
