package org.processmining.estminer.specpp.datastructures.tree.nodegen;

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
    public void filterPotentialSetExpansions(BitEncodedSet<Transition> expansions, boolean isPostsetExpansion) {
        if (expansions.isEmpty()) return;

        Function<Place, BitEncodedSet<Transition>> getTransitions = isPostsetExpansion ? Place::postset : Place::preset;
        Function<Place, BitEncodedSet<Transition>> getOtherTransitions = isPostsetExpansion ? Place::preset : Place::postset;

        for (Place wiredPlace : wiredPlaces) {
            if (getOtherTransitions.apply(wiredPlace).intersects(expansions)) {
                expansions.setminus(getTransitions.apply(wiredPlace));
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
