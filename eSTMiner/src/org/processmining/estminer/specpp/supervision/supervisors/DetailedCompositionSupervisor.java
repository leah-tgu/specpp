package org.processmining.estminer.specpp.supervision.supervisors;

import org.processmining.estminer.specpp.componenting.delegators.DelegatingObservable;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.composition.events.CandidateCompositionEvent;
import org.processmining.estminer.specpp.supervision.monitoring.EventCounterMonitor;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;

public class DetailedCompositionSupervisor extends MonitoringSupervisor {

    private DelegatingObservable<CandidateCompositionEvent<?>> compositionEvents = new DelegatingObservable<>();

    public DetailedCompositionSupervisor() {
        componentSystemAdapter().require(SupervisionRequirements.observable("composer.events", CandidateCompositionEvent.class), compositionEvents);
        createMonitor("composer.events", new EventCounterMonitor());
    }

    @Override
    protected void instantiateObservationHandlingFullySatisfied() {
        beginLaying().source(compositionEvents)
                     .pipe(PipeWorks.concurrencyBridge())
                     .giveBackgroundThread()
                     .sink(getMonitor("composer.events"))
                     .apply();
    }

}
