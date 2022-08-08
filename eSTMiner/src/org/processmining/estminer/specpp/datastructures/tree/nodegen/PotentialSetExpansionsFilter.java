package org.processmining.estminer.specpp.datastructures.tree.nodegen;

import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.estminer.specpp.datastructures.petri.Transition;

public interface PotentialSetExpansionsFilter {

    void filterPotentialSetExpansions(BitEncodedSet<Transition> expansions, PlaceGenerator.ExpansionType expansionType);


    void filterPotentialSetExpansions(BitMask expansions, PlaceGenerator.ExpansionType expansionType);
}
