package org.processmining.estminer.specpp.representations.petri;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.representations.encoding.BitEncodedSet;
import org.processmining.estminer.specpp.representations.encoding.NonMutatingSetOperations;
import org.processmining.estminer.specpp.representations.tree.base.NodeProperties;
import org.processmining.estminer.specpp.traits.Copyable;
import org.processmining.estminer.specpp.traits.ProperlyHashable;
import org.processmining.estminer.specpp.traits.ProperlyPrintable;

public class Place implements Candidate, NodeProperties, ProperlyHashable, ProperlyPrintable, Copyable<Place>, NonMutatingSetOperations<Place> {

    private final BitEncodedSet<Transition> ingoingTransitions, outgoingTransitions;

    public Place(BitEncodedSet<Transition> ingoingTransitions, BitEncodedSet<Transition> outgoingTransitions) {
        this.ingoingTransitions = ingoingTransitions;
        this.outgoingTransitions = outgoingTransitions;
    }

    public boolean isEmpty() {
        return ingoingTransitions.cardinality() == 0 && outgoingTransitions.cardinality() == 0;
    }

    public BitEncodedSet<Transition> preset() {
        return ingoingTransitions;
    }

    public BitEncodedSet<Transition> postset() {
        return outgoingTransitions;
    }

    @Override
    public Place union(Place other) {
        Place result = copy();
        result.preset().union(other.preset());
        result.postset().union(other.postset());
        return result;
    }

    @Override
    public Place setminus(Place other) {
        Place result = copy();
        result.preset().setminus(other.preset());
        result.postset().setminus(other.postset());
        return result;
    }

    @Override
    public Place intersection(Place other) {
        Place result = copy();
        result.preset().intersection(other.preset());
        result.postset().intersection(other.postset());
        return result;
    }

    public int size() {
        return preset().cardinality() + postset().cardinality();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        if (!ingoingTransitions.setEquality(place.ingoingTransitions)) return false;
        return outgoingTransitions.setEquality(place.outgoingTransitions);
    }

    @Override
    public int hashCode() {
        int result = ingoingTransitions.hashCode();
        result = 31 * result + outgoingTransitions.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return ingoingTransitions.toString() + "|" + outgoingTransitions.toString();
    }

    @Override
    public Place copy() {
        return new Place(ingoingTransitions.copy(), outgoingTransitions.copy());
    }
}
