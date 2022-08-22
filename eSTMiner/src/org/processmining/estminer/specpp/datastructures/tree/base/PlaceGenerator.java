package org.processmining.estminer.specpp.datastructures.tree.base;

import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.system.LocalComponentRepository;
import org.processmining.estminer.specpp.componenting.traits.UsesLocalComponentSystem;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.estminer.specpp.traits.Initializable;

public abstract class PlaceGenerator implements ConstrainableLocalNodeGenerator<Place, PlaceState, PlaceNode, GenerationConstraint>, UsesLocalComponentSystem {

    protected final LocalComponentRepository lcr = new LocalComponentRepository();

    @Override
    public ComponentCollection localComponentSystem() {
        return lcr;
    }

}
