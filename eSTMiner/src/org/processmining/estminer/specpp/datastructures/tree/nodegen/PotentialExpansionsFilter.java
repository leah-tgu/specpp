package org.processmining.estminer.specpp.datastructures.tree.nodegen;

import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.petri.Place;

public interface PotentialExpansionsFilter {

    BitMask filterPotentialSetExpansions(Place place, BitMask expansions, MonotonousPlaceGenerationLogic.ExpansionType expansionType);

}
