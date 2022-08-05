package org.processmining.estminer.specpp.datastructures.tree.constraints;

import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.base.GenerationConstraint;

public class WiringConstraint extends WiredPlace implements GenerationConstraint {

    public WiringConstraint(Place affectedPlace) {
        super(affectedPlace);
    }

}
