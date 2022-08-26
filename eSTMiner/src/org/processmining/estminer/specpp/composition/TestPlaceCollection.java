package org.processmining.estminer.specpp.composition;

import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.vectorization.VariantMarkingHistories;
import org.processmining.estminer.specpp.evaluation.implicitness.ImplicitnessRating;
import org.processmining.estminer.specpp.evaluation.implicitness.ReplayBasedImplicitnessCalculator;

public class TestPlaceCollection extends PlaceCollection {
    @Override
    public ImplicitnessRating rateImplicitness(Place place) {
        timeStopper.start(PlaceCollection.REPLAY_BASED_CONCURRENT_IMPLICITNESS);
        VariantMarkingHistories h = historyMaker.eval(place);
        BitMask mask = getCurrentlySupportedVariants();
        ImplicitnessRating implicitnessRating = ReplayBasedImplicitnessCalculator.replaySubregionImplicitnessOn(mask, place, h, histories);
        timeStopper.stop(PlaceCollection.REPLAY_BASED_CONCURRENT_IMPLICITNESS);
        return implicitnessRating;
    }
}
