package org.processmining.estminer.specpp.representations.tree.constraints;

import org.processmining.estminer.specpp.representations.petri.Place;

public class AddWiredPlace extends WiringConstraint {
    public AddWiredPlace(Place affectedPlace) {
        super(affectedPlace);
    }
}
