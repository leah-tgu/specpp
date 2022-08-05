package org.processmining.estminer.specpp.datastructures.tree.constraints;

import org.processmining.estminer.specpp.datastructures.petri.Place;

public class AddWiredPlace extends WiringConstraint {
    public AddWiredPlace(Place affectedPlace) {
        super(affectedPlace);
    }
}
