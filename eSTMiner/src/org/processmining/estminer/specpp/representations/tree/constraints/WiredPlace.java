package org.processmining.estminer.specpp.representations.tree.constraints;

import org.processmining.estminer.specpp.base.impls.CandidateConstraint;
import org.processmining.estminer.specpp.representations.petri.Place;

public class WiredPlace extends CandidateConstraint<Place> {

    public WiredPlace(Place affectedPlace) {
        super(affectedPlace);
    }
}
