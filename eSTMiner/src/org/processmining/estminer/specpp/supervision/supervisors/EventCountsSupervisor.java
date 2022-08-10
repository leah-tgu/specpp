package org.processmining.estminer.specpp.supervision.supervisors;

import org.processmining.estminer.specpp.base.ConstraintEvent;
import org.processmining.estminer.specpp.base.impls.CandidateConstraint;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingObservable;
import org.processmining.estminer.specpp.datastructures.tree.base.GenerationConstraint;
import org.processmining.estminer.specpp.datastructures.tree.events.HeuristicComputationEvent;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.estminer.specpp.supervision.monitoring.KeepLastMonitor;
import org.processmining.estminer.specpp.supervision.observations.EventCountStatistics;
import org.processmining.estminer.specpp.supervision.observations.TreeEvent;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.supervision.transformers.Transformers;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

import java.time.Duration;

import static org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements.observable;

public class EventCountsSupervisor extends MonitoringSupervisor {

    protected final DelegatingObservable<ConstraintEvent> composerConstraints = new DelegatingObservable<>();
    protected final DelegatingObservable<ConstraintEvent> proposerConstraints = new DelegatingObservable<>();
    protected final DelegatingObservable<TreeEvent> treeEvents = new DelegatingObservable<>();
    protected final DelegatingObservable<HeuristicComputationEvent<DoubleScore>> heuristicsEvents = new DelegatingObservable<>();

    public EventCountsSupervisor() {
        componentSystemAdapter().require(observable("tree.events", TreeEvent.class), treeEvents)
                                .require(observable("composer.constraints", JavaTypingUtils.castClass(CandidateConstraint.class)), composerConstraints)
                                .require(observable("proposer.constraints", GenerationConstraint.class), proposerConstraints)
                                .require(observable("heuristics.events", JavaTypingUtils.castClass(HeuristicComputationEvent.class)), heuristicsEvents);
        createMonitor("tree.events.accumulation", new KeepLastMonitor<>());
        createMonitor("heuristics.count.accumulation", new KeepLastMonitor<>());
        createMonitor("composer.constraints.count.accumulation", new KeepLastMonitor<>());
        createMonitor("composer.constraints.count.accumulation", new KeepLastMonitor<>());
    }


    @Override
    protected void instantiateObservationHandlingFullySatisfied() {
        instantiateObservationHandlingPartiallySatisfied();
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
                         .sink(PipeWorks.loggingSink(RefreshRates.REFRESH_STRING + "heuristic.count", fileLogger))
                         .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                         .sinks(PipeWorks.loggingSinks("heuristics.count.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("heuristics.count.accumulation"))
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
                         .sinks(PipeWorks.loggingSinks("composer.constraints.count.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("composer.constraints.count.accumulation"))
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
                         .sinks(PipeWorks.loggingSinks("proposer.constraints.count.accumulation", EventCountStatistics::toPrettyString, consoleLogger, fileLogger))
                         .sink(getMonitor("composer.constraints.count.accumulation"))
                         .apply();
        }
    }
}
