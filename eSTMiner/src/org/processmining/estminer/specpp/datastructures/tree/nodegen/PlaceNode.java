package org.processmining.estminer.specpp.datastructures.tree.nodegen;

import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.base.LocalNodeGenerator;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.GeneratingLocalNode;

import java.util.UUID;

public class PlaceNode extends GeneratingLocalNode<Place, PlaceState, PlaceNode> {

    public PlaceNode(Place place, PlaceState state, LocalNodeGenerator<Place, PlaceState, PlaceNode> generator, boolean isRoot, int globalId, int depth) {
        super(isRoot, place, state, generator, globalId, depth);
    }

    public static PlaceNode root(Place place, LocalNodeGenerator<Place, PlaceState, PlaceNode> generator) {
        return new PlaceNode(place, PlaceState.initialState(), generator, true, 0, 0);
    }

    public PlaceNode child(Place childPlace, PlaceState childState) {
        return new PlaceNode(childPlace, childState, getGenerator(), false, UUID.randomUUID()
                                                                                .hashCode(), getDepth() + 1);
    }

    public PlaceNode child(Place childPlace) {
        return new PlaceNode(childPlace, PlaceState.initialState(), getGenerator(), false, UUID.randomUUID()
                                                                                               .hashCode(), getDepth() + 1);
    }

    public PlaceNode parent(Place parentPlace, PlaceState parentState, boolean isRoot) {
        return new PlaceNode(parentPlace, parentState, getGenerator(), isRoot, UUID.randomUUID()
                                                                                   .hashCode(), getDepth() - 1);
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
        return getState().getPostsetChildrenMask().cardinality() > 0 || getState().getPresetChildrenMask()
                                                                                  .cardinality() > 0;
    }

    @Override
    public boolean canExpand() {
        return getGenerator().hasChildrenLeft(this);
    }

    @Override
    public PlaceNode generateChild() {
        return getGenerator().generateChild(this);
    }

}
