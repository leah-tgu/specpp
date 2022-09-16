package org.processmining.specpp.datastructures.petri;

import com.google.common.collect.ImmutableSet;
import org.processmining.specpp.base.Result;

import java.util.Collection;
import java.util.Set;

public class PetriNet implements Result {
    private final Set<Place> places;

    public PetriNet(Collection<Place> places) {
        this.places = ImmutableSet.copyOf(places);
    }

    public Set<Place> getPlaces() {
        return places;
    }

    @Override
    public String toString() {
        return "PetriNet{" + "places=" + places + '}';
    }

    public int size() {
        return places.size();
    }
}
