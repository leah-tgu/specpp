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
                         .sink(PipeWorks.loggingSink("heuristic.events.count", fileLogger))
                         .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                         .sinks(PipeWorks.loggingSinks("heuristics.events.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("heuristics.events.accumulation"))
                         .apply();
        }

        if (compositionEvents.isSet()) {
            beginLaying().source(compositionEvents)
                         .pipe(PipeWorks.concurrencyBridge())
                         .giveBackgroundThread()
                         .pipe(PipeWorks.selfEmptyingSummarizingBuffer(Transformers.eventCounter(), CAPACITY))
                         .sinks(PipeWorks.loggingSinks("composition.events.count", fileLogger))
                         .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                         .sinks(PipeWorks.loggingSinks("composition.events.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("composition.events.accumulation"))
                         .apply();
        }

        if (composerConstraints.isSet()) {
            beginLaying().source(composerConstraints)
                         .pipe(PipeWorks.concurrencyBridge())
                         .giveBackgroundThread()
                         .pipe(PipeWorks.selfEmptyingSummarizingBuffer(Transformers.eventCounter(), CAPACITY))
                         .sinks(PipeWorks.loggingSinks("composer.constraints.count", fileLogger))
                         .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                         .sinks(PipeWorks.loggingSinks("composer.constraints.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("composer.constraints.accumulation"))
                         .apply();
        }

        if (compositionConstraints.isSet()) {
            beginLaying().source(compositionConstraints)
                         .pipe(PipeWorks.concurrencyBridge())
                         .giveBackgroundThread()
                         .pipe(PipeWorks.selfEmptyingSummarizingBuffer(Transformers.eventCounter(), CAPACITY))
                         .sinks(PipeWorks.loggingSinks("composition.constraints.count", fileLogger))
                         .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                         .sinks(PipeWorks.loggingSinks("composition.constraints.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("composition.constraints.accumulation"))
                         .apply();
        }

        if (proposerConstraints.isSet()) {
            beginLaying().source(proposerConstraints)
                         .pipe(PipeWorks.concurrencyBridge())
                         .giveBackgroundThread()
                         .pipe(PipeWorks.selfEmptyingSummarizingBuffer(Transformers.eventCounter(), CAPACITY))
                         .sinks(PipeWorks.loggingSinks("proposer.constraints.count", fileLogger))
                         .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                         .sinks(PipeWorks.loggingSinks("proposer.constraints.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("proposer.constraints.accumulation"))
                         .apply();
        }

    }
}
