package org.processmining.estminer.specpp.supervision.supervisors;

import org.processmining.estminer.specpp.componenting.delegators.ContainerUtils;
import org.processmining.estminer.specpp.supervision.monitoring.PerformanceStatisticsMonitor;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceStatistics;
import org.processmining.estminer.specpp.supervision.piping.ConcurrencyBridge;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.supervision.transformers.Transformers;

import static org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements.observable;
import static org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements.regex;

public class PerformanceSupervisor extends MonitoringSupervisor {

    private final ConcurrencyBridge<PerformanceEvent> performanceEventConcurrencyBridge = PipeWorks.concurrencyBridge();

    public PerformanceSupervisor() {
        componentSystemAdapter().require(observable(regex("^.+\\.performance$"), PerformanceEvent.class), ContainerUtils.observeResults(performanceEventConcurrencyBridge));

        createMonitor("performance", new PerformanceStatisticsMonitor());
    }

    @Override
    protected void instantiateObservationHandlingFullySatisfied() {
        beginLaying().source(performanceEventConcurrencyBridge)
                     .giveBackgroundThread()
                     .pipe(PipeWorks.summarizingBuffer(Transformers.performanceEventSummarizer()))
                     .schedule(RefreshRates.REFRESH_INTERVAL)
                     .sinks(PipeWorks.loggingSinks(RefreshRates.REFRESH_STRING + " performance", PerformanceStatistics::toPrettyString, consoleLogger, fileLogger))
                     .pipe(PipeWorks.accumulatingPipe(PerformanceStatistics::new))
                     .sinks(PipeWorks.loggingSinks("performance.accumulation", PerformanceStatistics::toPrettyString, consoleLogger, fileLogger))
                     .sink(getMonitor("performance"))
                     .apply();
    }
}