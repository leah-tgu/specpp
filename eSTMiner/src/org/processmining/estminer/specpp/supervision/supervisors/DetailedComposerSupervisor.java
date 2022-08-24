package org.processmining.estminer.specpp.supervision.supervisors;

import org.processmining.estminer.specpp.componenting.delegators.ContainerUtils;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.composition.events.CandidateCompositionEvent;
import org.processmining.estminer.specpp.supervision.monitoring.EventCounterMonitor;
import org.processmining.estminer.specpp.supervision.piping.IdentityPipe;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;

public class DetailedComposerSupervisor extends MonitoringSupervisor {

    private final IdentityPipe<CandidateCompositionEvent<?>> collector = PipeWorks.identityPipe();

    public DetailedComposerSupervisor() {
        componentSystemAdapter().require(SupervisionRequirements.observable(SupervisionRequirements.regex("^composer.*\\.events$"), CandidateCompositionEvent.class), ContainerUtils.observeResults(collector));
        createMonitor("composer.events", new EventCounterMonitor());
    }

    @Override
    protected void instantiateObservationHandlingFullySatisfied() {
        beginLaying().source(collector)
                     .pipe(PipeWorks.concurrencyBridge())
                     .giveBackgroundThread()
                     .sink(getMonitor("composer.events"))
                     .apply();
    }

}
