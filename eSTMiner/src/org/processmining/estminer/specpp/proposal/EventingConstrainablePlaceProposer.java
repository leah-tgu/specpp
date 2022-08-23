package org.processmining.estminer.specpp.proposal;

import org.processmining.estminer.specpp.componenting.traits.HasComponentCollection;
import org.processmining.estminer.specpp.datastructures.tree.base.PlaceGenerationLogic;

public class EventingConstrainablePlaceProposer extends ConstrainablePlaceProposer implements HasComponentCollection {

    public EventingConstrainablePlaceProposer(PlaceProposer<? extends PlaceGenerationLogic> delegate) {
        super(delegate);
    }

}
