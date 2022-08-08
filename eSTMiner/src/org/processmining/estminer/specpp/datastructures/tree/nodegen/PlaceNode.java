package org.processmining.estminer.specpp.datastructures.tree.nodegen;

import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.base.LocalNodeGenerator;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.GeneratingLocalNode;

import java.util.Optional;

/**
 * This class represents a tree node containing a {@code Place} together with a {@code PlaceState} that indicates already generated children nodes.
 * It is an implementation of a {@code GeneratingLocalNode}, that is, it does not hold references to any other nodes and instead employs only its local state and a {@code PlaceGenerator} to compute unseen children.
 * @see Place
 * @see PlaceState
 * @see GeneratingLocalNode
 * @see PlaceGenerator
 */
public class PlaceNode extends GeneratingLocalNode<Place, PlaceState, PlaceNode> {

    protected PlaceNode(Place place, PlaceState state, LocalNodeGenerator<Place, PlaceState, PlaceNode> generator, boolean isRoot, int depth) {
        super(isRoot, place, state, generator, depth);
    }

    protected static PlaceNode root(Place place, PlaceState state, LocalNodeGenerator<Place, PlaceState, PlaceNode> generator) {
        return new PlaceNode(place, state, generator, true, 0);
    }


    public PlaceNode makeChild(Place childPlace, PlaceState childState) {
        return new PlaceNode(childPlace, childState, getGenerator(), false, getDepth() + 1);
    }

    public Place getPlace() {
        return getProperties();
    }

    @Override
    public PlaceNode generateParent() {
        return getGenerator().generateParent(this);
    }

    @Override
    public Iterable<PlaceNode> generatePotentialChildren() {
        return getGenerator().potentialChildren(this);
    }

    @Override
    public boolean didExpand() {
        return !getState().isCurrentlyALeaf();
    }

    @Override
    protected boolean canExpandBasedOnGenerator() {
        return getGenerator().hasChildrenLeft(this);
    }

    @Override
    protected Optional<Boolean> canExpandBasedOnState() {
        PlaceState state = getState();
        return state.isCertainlyALeaf() ? Optional.of(Boolean.FALSE) : Optional.empty();
    }

    @Override
    public PlaceNode generateChild() {
        return getGenerator().generateChild(this);
    }
}
