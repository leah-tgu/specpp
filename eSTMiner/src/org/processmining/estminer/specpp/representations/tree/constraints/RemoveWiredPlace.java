package org.processmining.estminer.specpp.representations.tree.constraints;

import org.processmining.estminer.specpp.representations.petri.Place;

public class RemoveWiredPlace extends WiringConstraint {
    public RemoveWiredPlace(Place affectedPlace) {
        super(affectedPlace);
    }
}
