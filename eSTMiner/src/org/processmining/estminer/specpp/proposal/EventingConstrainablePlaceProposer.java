package org.processmining.estminer.specpp.proposal;

import org.processmining.estminer.specpp.componenting.traits.HasComponentCollection;
import org.processmining.estminer.specpp.datastructures.tree.base.PlaceGenerator;

public class EventingConstrainablePlaceProposer extends ConstrainablePlaceProposer implements HasComponentCollection {

    public EventingConstrainablePlaceProposer(PlaceProposer<? extends PlaceGenerator> delegate) {
        super(delegate);
    }

}
