package org.processmining.estminer.specpp.proposal;

import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.componenting.traits.UsesComponentSystem;
import org.processmining.estminer.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.datastructures.tree.base.PlaceGenerator;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;

public class EventingConstrainablePlaceProposer extends ConstrainablePlaceProposer implements UsesComponentSystem {

    public EventingConstrainablePlaceProposer(PlaceProposer<? extends PlaceGenerator> delegate) {
        super(delegate);
    }

}
