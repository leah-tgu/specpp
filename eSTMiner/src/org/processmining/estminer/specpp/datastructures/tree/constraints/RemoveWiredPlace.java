package org.processmining.estminer.specpp.datastructures.tree.constraints;

import org.processmining.estminer.specpp.datastructures.petri.Place;

public class RemoveWiredPlace extends WiringConstraint {
    public RemoveWiredPlace(Place affectedPlace) {
        super(affectedPlace);
    }
}
