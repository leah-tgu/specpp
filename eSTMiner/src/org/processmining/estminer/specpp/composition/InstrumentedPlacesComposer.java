package org.processmining.estminer.specpp.composition;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.composition.events.*;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

public class InstrumentedPlacesComposer<I extends AdvancedComposition<Place>> extends PlacesComposer<I> {

    private final EventSupervision<CandidateCompositionEvent<Place>> compositionEventSupervision = PipeWorks.eventSupervision();

    public InstrumentedPlacesComposer(I placeComposition) {
        super(placeComposition);
        componentSystemAdapter().provide(SupervisionRequirements.observable("composer.events", JavaTypingUtils.castClass(CandidateCompositionEvent.class), compositionEventSupervision));
    }

    @Override
    protected void candidateAccepted(Place candidate) {
        super.candidateAccepted(candidate);
        compositionEventSupervision.observe(new CandidateAccepted<>(candidate));
    }

    @Override
    protected void candidateRejected(Place candidate) {
        super.candidateRejected(candidate);
        compositionEventSupervision.observe(new CandidateRejected<>(candidate));
    }

    @Override
    protected void acceptanceRevoked(Place candidate) {
        super.acceptanceRevoked(candidate);
        compositionEventSupervision.observe(new CandidateAcceptanceRevoked<>(candidate));
    }
}
