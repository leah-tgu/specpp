package org.processmining.estminer.specpp.representations.tree.constraints;

import org.processmining.estminer.specpp.representations.petri.Place;
import org.processmining.estminer.specpp.representations.tree.base.GenerationConstraint;

public class WiringConstraint extends WiredPlace implements GenerationConstraint {

    public WiringConstraint(Place affectedPlace) {
        super(affectedPlace);
    }

}
