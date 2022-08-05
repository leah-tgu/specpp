package org.processmining.estminer.specpp.representations.tree.nodegen;

import org.processmining.estminer.specpp.representations.encoding.BitEncodedSet;
import org.processmining.estminer.specpp.representations.petri.Transition;

public interface PotentialSetExpansionsFilter {

    void filterPotentialSetExpansions(BitEncodedSet<Transition> expansions, boolean isPostsetExpansion);


}
