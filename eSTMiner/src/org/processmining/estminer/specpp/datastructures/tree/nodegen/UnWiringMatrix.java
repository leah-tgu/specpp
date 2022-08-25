package org.processmining.estminer.specpp.datastructures.tree.nodegen;

import org.processmining.estminer.specpp.datastructures.encoding.IntEncodings;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.Transition;

import java.util.HashSet;
import java.util.Set;

public class UnWiringMatrix extends WiringMatrix {

    protected final Set<Place> wiredPlaces;

    public UnWiringMatrix(IntEncodings<Transition> transitionEncodings) {
        super(transitionEncodings);
        wiredPlaces = new HashSet<>();
    }

    @Override
    public void wire(Place place) {
        super.wire(place);
        wiredPlaces.add(place);
    }

    private void recomputeWireSets() {
        for (Place p : wiredPlaces) {
            wire(p);
        }
    }

    @Override
    public void unwire(Place place) {
        wiredPlaces.remove(place);
        recomputeWireSets();
    }

}
