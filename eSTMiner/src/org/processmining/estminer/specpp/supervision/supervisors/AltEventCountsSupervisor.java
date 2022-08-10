package org.processmining.estminer.specpp.supervision.supervisors;

import org.processmining.estminer.specpp.supervision.observations.EventCountStatistics;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.supervision.transformers.Transformers;

public class AltEventCountsSupervisor extends EventCountsSupervisor {


    private static final int CAPACITY = 10_000;

    @Override
    protected void instantiateObservationHandlingPartiallySatisfied() {

        if (treeEvents.isSet()) {
            beginLaying().source(treeEvents)
                         .pipe(PipeWorks.concurrencyBridge())
                         .giveBackgroundThread()
                         .pipe(PipeWorks.selfEmptyingSummarizingBuffer(Transformers.eventCounter(), CAPACITY))
                         .sinks(PipeWorks.loggingSinks("tree.events.count", fileLogger))
                         .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                         .sinks(PipeWorks.loggingSinks("tree.events.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("tree.events.accumulation"))
                         .apply();
        }

        if (heuristicsEvents.isSet()) {
            beginLaying().source(heuristicsEvents)
                         .pipe(PipeWorks.concurrencyBridge())
                         .giveBackgroundThread()
                         .pipe(PipeWorks.selfEmptyingSummarizingBuffer(Transformers.eventCounter(), CAPACITY))
                         .sink(PipeWorks.loggingSink("heuristic.count", fileLogger))
                         .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                         .sinks(PipeWorks.loggingSinks("heuristics.count.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("heuristics.count.accumulation"))
                         .apply();
        }

        if (composerConstraints.isSet()) {
            beginLaying().source(composerConstraints)
                         .pipe(PipeWorks.concurrencyBridge())
                         .giveBackgroundThread()
                         .pipe(PipeWorks.selfEmptyingSummarizingBuffer(Transformers.eventCounter(), CAPACITY))
                         .sinks(PipeWorks.loggingSinks("composer.constraints.count", fileLogger))
                         .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                         .sinks(PipeWorks.loggingSinks("composer.constraints.count.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("composer.constraints.count.accumulation"))
                         .apply();
        }

        if (proposerConstraints.isSet()) {
            beginLaying().source(proposerConstraints)
                         .pipe(PipeWorks.concurrencyBridge())
                         .giveBackgroundThread()
                         .pipe(PipeWorks.selfEmptyingSummarizingBuffer(Transformers.eventCounter(), CAPACITY))
                         .sinks(PipeWorks.loggingSinks("proposer.constraints.count", fileLogger))
                         .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                         .sinks(PipeWorks.loggingSinks("proposer.constraints.count.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("composer.constraints.count.accumulation"))
                         .apply();
        }

    }
}
