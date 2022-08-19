package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.evaluation.implicitness.ImplicitnessRating;

public interface PlaceCollectionLocalInfo {

    ImplicitnessRating rateImplicitness(Place place);

    BitMask getCurrentSupportedVariants();

}
