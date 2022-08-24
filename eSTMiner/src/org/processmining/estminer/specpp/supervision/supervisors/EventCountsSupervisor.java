package org.processmining.estminer.specpp.supervision.supervisors;

import org.processmining.estminer.specpp.base.ConstraintEvent;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingObservable;
import org.processmining.estminer.specpp.composition.events.CandidateCompositionEvent;
import org.processmining.estminer.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.datastructures.tree.events.HeuristicComputationEvent;
import org.processmining.estminer.specpp.datastructures.tree.events.TreeEvent;
import org.processmining.estminer.specpp.supervision.monitoring.KeepLastMonitor;
import org.processmining.estminer.specpp.supervision.observations.EventCountStatistics;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.supervision.transformers.Transformers;

import static org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements.observable;
import static org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements.regex;

public class EventCountsSupervisor extends MonitoringSupervisor {

    protected final DelegatingObservable<ConstraintEvent> composerConstraints = new DelegatingObservable<>();
    protected final DelegatingObservable<ConstraintEvent> compositionConstraints = new DelegatingObservable<>();
    protected final DelegatingObservable<ConstraintEvent> proposerConstraints = new DelegatingObservable<>();
    protected final DelegatingObservable<TreeEvent> treeEvents = new DelegatingObservable<>();
    protected final DelegatingObservable<CandidateCompositionEvent<?>> compositionEvents = new DelegatingObservable<>();
    protected final DelegatingObservable<HeuristicComputationEvent<?>> heuristicsEvents = new DelegatingObservable<>();

    public EventCountsSupervisor() {
        componentSystemAdapter().require(observable(regex("tree\\.events.*"), TreeEvent.class), treeEvents)
                                .require(observable(regex("composer\\.events.*"), CandidateCompositionEvent.class), compositionEvents)
                                .require(observable(regex("composer\\.constraints.*"), ConstraintEvent.class), composerConstraints)
                                .require(observable(regex("composition\\.constraints.*"), ConstraintEvent.class), compositionConstraints)
                                .require(observable(regex("proposer\\.constraints.*"), GenerationConstraint.class), proposerConstraints)
                                .require(observable(regex("heuristics\\.events.*"), HeuristicComputationEvent.class), heuristicsEvents);
        createMonitor("tree.events.accumulation", new KeepLastMonitor<>());
        createMonitor("composer.events.accumulation", new KeepLastMonitor<>());
        createMonitor("composition.events.accumulation", new KeepLastMonitor<>());
        createMonitor("heuristics.events.accumulation", new KeepLastMonitor<>());
        createMonitor("proposer.constraints.accumulation", new KeepLastMonitor<>());
        createMonitor("composer.constraints.accumulation", new KeepLastMonitor<>());
        createMonitor("composition.constraints.accumulation", new KeepLastMonitor<>());
    }


    @Override
    protected void instantiateObservationHandlingPartiallySatisfied() {
        if (treeEvents.isSet()) {
            beginLaying().source(treeEvents)
                         .pipe(PipeWorks.concurrencyBridge())
                         .giveBackgroundThread()
                         .pipe(PipeWorks.summarizingBuffer(Transformers.eventCounter()))
                         .schedule(RefreshRates.REFRESH_INTERVAL)
                         .sinks(PipeWorks.loggingSinks(RefreshRates.REFRESH_STRING + "tree.events.count", fileLogger))
                         .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                         .sinks(PipeWorks.loggingSinks("tree.events.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("tree.events.accumulation"))
                         .apply();
        }

        if (heuristicsEvents.isSet()) {
            beginLaying().source(heuristicsEvents)
                         .pipe(PipeWorks.concurrencyBridge())
                         .giveBackgroundThread()
                         .pipe(PipeWorks.summarizingBuffer(Transformers.eventCounter()))
                         .schedule(RefreshRates.REFRESH_INTERVAL)
                         .sink(PipeWorks.loggingSink(RefreshRates.REFRESH_STRING + "heuristic.events.count", fileLogger))
                         .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                         .sinks(PipeWorks.loggingSinks("heuristics.events.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("heuristics.events.accumulation"))
                         .apply();
        }

        if (compositionEvents.isSet()) {
            beginLaying().source(compositionEvents)
                         .pipe(PipeWorks.concurrencyBridge())
                         .giveBackgroundThread()
                         .pipe(PipeWorks.summarizingBuffer(Transformers.eventCounter()))
                         .schedule(RefreshRates.REFRESH_INTERVAL)
                         .sinks(PipeWorks.loggingSinks(RefreshRates.REFRESH_STRING + "composition.events.count", fileLogger))
                         .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                         .sinks(PipeWorks.loggingSinks("composition.events.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("composition.events.accumulation"))
                         .apply();
        }

        if (composerConstraints.isSet()) {
            beginLaying().source(composerConstraints)
                         .pipe(PipeWorks.concurrencyBridge())
                         .giveBackgroundThread()
                         .pipe(PipeWorks.summarizingBuffer(Transformers.eventCounter()))
                         .schedule(RefreshRates.REFRESH_INTERVAL)
                         .sinks(PipeWorks.loggingSinks(RefreshRates.REFRESH_STRING + "composer.constraints.count", fileLogger))
                         .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                         .sinks(PipeWorks.loggingSinks("composer.constraints.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("composer.constraints.accumulation"))
                         .apply();
        }

        if (compositionConstraints.isSet()) {
            beginLaying().source(compositionConstraints)
                         .pipe(PipeWorks.concurrencyBridge())
                         .giveBackgroundThread()
                         .pipe(PipeWorks.summarizingBuffer(Transformers.eventCounter()))
                         .schedule(RefreshRates.REFRESH_INTERVAL)
                         .sinks(PipeWorks.loggingSinks(RefreshRates.REFRESH_STRING + "composition.constraints.count", fileLogger))
                         .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                         .sinks(PipeWorks.loggingSinks("composition.constraints.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("composition.constraints.accumulation"))
                         .apply();
        }

        if (proposerConstraints.isSet()) {
            beginLaying().source(proposerConstraints)
                         .pipe(PipeWorks.concurrencyBridge())
                         .giveBackgroundThread()
                         .pipe(PipeWorks.summarizingBuffer(Transformers.eventCounter()))
                         .schedule(RefreshRates.REFRESH_INTERVAL)
                         .sinks(PipeWorks.loggingSinks(RefreshRates.REFRESH_STRING + "proposer.constraints.count", fileLogger))
                         .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                         .sinks(PipeWorks.loggingSinks("proposer.constraints.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("proposer.constraints.accumulation"))
                         .apply();
        }
    }
}
