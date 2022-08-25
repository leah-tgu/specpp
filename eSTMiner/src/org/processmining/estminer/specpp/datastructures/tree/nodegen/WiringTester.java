package org.processmining.estminer.specpp.datastructures.tree.nodegen;

import org.processmining.estminer.specpp.datastructures.petri.Place;

public interface WiringTester extends PotentialExpansionsFilter, ExpansionStopper {
    void wire(Place place);
    void unwire(Place place);
}
