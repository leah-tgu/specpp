package org.processmining.estminer.specpp.datastructures.tree.nodegen;

import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.BitEncodedSet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.Transition;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

public class WiringTester implements PotentialSetExpansionsFilter {

    private final ArrayList<Place> wiredPlaces;

    public WiringTester() {
        this.wiredPlaces = new ArrayList<>();
    }

    @Override
    public void filterPotentialSetExpansions(BitEncodedSet<Transition> expansions, PlaceGenerator.ExpansionType expansionType) {
        filterPotentialSetExpansions(expansions.getBitMask(), expansionType);
    }

    @Override
    public void filterPotentialSetExpansions(BitMask expansions, PlaceGenerator.ExpansionType expansionType) {
        if (expansions.isEmpty()) return;

        Function<Place, BitEncodedSet<Transition>> getTransitions = expansionType == PlaceGenerator.ExpansionType.Postset ? Place::postset : Place::preset;
        Function<Place, BitEncodedSet<Transition>> getOtherTransitions = expansionType == PlaceGenerator.ExpansionType.Postset ? Place::preset : Place::postset;

        for (Place wiredPlace : wiredPlaces) {
            if (getOtherTransitions.apply(wiredPlace).getBitMask().intersects(expansions)) {
                expansions.setminus(getTransitions.apply(wiredPlace).getBitMask());
            }
        }
    }

    private Predicate<Place> overlappingWiringPredicate(Place testPlace) {
        final BitEncodedSet<Transition> preset = testPlace.preset();
        final BitEncodedSet<Transition> postset = testPlace.postset();
        return wiredPlace -> preset.intersects(wiredPlace.preset()) && postset.intersects(wiredPlace.postset());
    }

    private boolean meetsWiringConstraint(Place place) {
        Predicate<Place> isOverlapping = overlappingWiringPredicate(place);
        return wiredPlaces.stream().noneMatch(isOverlapping);
    }

    public void wire(Place place) {
        wiredPlaces.add(place);
    }

    public void unwire(Place place) {
        wiredPlaces.remove(place);
    }

}
