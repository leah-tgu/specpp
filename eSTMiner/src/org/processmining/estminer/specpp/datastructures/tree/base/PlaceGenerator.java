package org.processmining.estminer.specpp.datastructures.tree.base;

import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceState;

public abstract class PlaceGenerator implements ConstrainableLocalNodeGenerator<Place, PlaceState, PlaceNode, GenerationConstraint> {
}
