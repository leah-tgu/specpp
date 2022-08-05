package org.processmining.estminer.specpp.datastructures.tree.constraints;

import org.processmining.estminer.specpp.base.impls.CandidateConstraint;
import org.processmining.estminer.specpp.datastructures.petri.Place;

public class WiredPlace extends CandidateConstraint<Place> {

    public WiredPlace(Place affectedPlace) {
        super(affectedPlace);
    }
}
