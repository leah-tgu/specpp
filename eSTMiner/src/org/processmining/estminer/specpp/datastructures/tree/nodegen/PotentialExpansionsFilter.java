package org.processmining.estminer.specpp.datastructures.tree.nodegen;

import org.processmining.estminer.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.petri.Transition;

public interface PotentialExpansionsFilter {

    void filterPotentialSetExpansions(BitEncodedSet<Transition> expansions, MonotonousPlaceGenerator.ExpansionType expansionType);


    void filterPotentialSetExpansions(BitMask expansions, MonotonousPlaceGenerator.ExpansionType expansionType);
}
