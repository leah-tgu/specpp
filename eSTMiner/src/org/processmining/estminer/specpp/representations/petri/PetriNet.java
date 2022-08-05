package org.processmining.estminer.specpp.representations.petri;

import org.processmining.estminer.specpp.base.Result;

import java.util.Set;

public class PetriNet implements Result {
    private final Set<Place> places;

    public PetriNet(Set<Place> places) {
        this.places = places;
    }

    public Set<Place> getPlaces() {
        return places;
    }
}
