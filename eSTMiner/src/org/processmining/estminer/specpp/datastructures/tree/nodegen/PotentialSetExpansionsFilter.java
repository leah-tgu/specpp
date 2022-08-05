package org.processmining.estminer.specpp.datastructures.tree.nodegen;

import org.processmining.estminer.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.estminer.specpp.datastructures.petri.Transition;

public interface PotentialSetExpansionsFilter {

    void filterPotentialSetExpansions(BitEncodedSet<Transition> expansions, boolean isPostsetExpansion);


}
