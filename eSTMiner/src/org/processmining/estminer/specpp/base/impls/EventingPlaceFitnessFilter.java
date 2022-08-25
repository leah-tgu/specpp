package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.link.ComposerComponent;
import org.processmining.estminer.specpp.componenting.system.link.CompositionComponent;
import org.processmining.estminer.specpp.composition.events.CandidateCompositionEvent;
import org.processmining.estminer.specpp.composition.events.CandidateRejected;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

public class EventingPlaceFitnessFilter<I extends CompositionComponent<Place>, R extends Result> extends PlaceFitnessFilter<I, R> {

    private final EventSupervision<CandidateCompositionEvent<Place>> composerEventSupervision = PipeWorks.eventSupervision();


    public EventingPlaceFitnessFilter(ComposerComponent<Place, I, R> childComposer) {
        super(childComposer);
        globalComponentSystem().provide(SupervisionRequirements.observable("composer.filter.events", JavaTypingUtils.castClass(CandidateCompositionEvent.class), composerEventSupervision));
    }

    @Override
    protected void gotFiltered(Place place) {
        super.gotFiltered(place);
        composerEventSupervision.observe(new CandidateRejected<>(place));
    }
}
