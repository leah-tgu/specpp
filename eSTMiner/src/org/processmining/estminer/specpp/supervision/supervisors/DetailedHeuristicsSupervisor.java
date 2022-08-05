package org.processmining.estminer.specpp.supervision.supervisors;

import org.processmining.estminer.specpp.componenting.delegators.DelegatingAdHocObservable;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingObservable;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.datastructures.tree.events.HeuristicComputationEvent;
import org.processmining.estminer.specpp.datastructures.tree.events.TreeHeuristicQueueingEvent;
import org.processmining.estminer.specpp.datastructures.tree.events.TreeHeuristicsEvent;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.supervision.CSVLogger;
import org.processmining.estminer.specpp.supervision.monitoring.TimeSeriesMonitor;
import org.processmining.estminer.specpp.supervision.observations.HeuristicStatsEvent;
import org.processmining.estminer.specpp.supervision.observations.TimedObservation;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

import java.time.Duration;
import java.time.LocalDateTime;

public class DetailedHeuristicsSupervisor extends MonitoringSupervisor {

    private final DelegatingAdHocObservable<HeuristicStatsEvent> heuristicStats = new DelegatingAdHocObservable<>();
    private final DelegatingObservable<TreeHeuristicsEvent> heuristicsEvents = new DelegatingObservable<>();

    public DetailedHeuristicsSupervisor() {
        componentSystemAdapter().require(SupervisionRequirements.observable("heuristics.events", JavaTypingUtils.<HeuristicComputationEvent<DoubleScore>>castClass(HeuristicComputationEvent.class)), heuristicsEvents)
                                .require(SupervisionRequirements.adHocObservable("heuristics.stats", HeuristicStatsEvent.class), heuristicStats);
        createMonitor("heuristics.queue.size", new TimeSeriesMonitor<>("queue.size", TimeSeriesMonitor.<TreeHeuristicQueueingEvent<PlaceNode>>delta_accumulator()));
    }

    @Override
    public void instantiateObservationHandling() {
        CSVLogger<TreeHeuristicQueueingEvent<PlaceNode>> queueSizeChanges = new CSVLogger<>("queue.csv", new String[]{"time", "place", "change", "queue.size delta"}, e -> new String[]{LocalDateTime.now().toString(), e.getSource()
                                                                                                                                                                                                                         .getProperties().toString(), e.getClass().getSimpleName(), Integer.toString(e.getDelta())});

        CSVLogger<TimedObservation<HeuristicComputationEvent<DoubleScore>>> heuristicsLogger = new CSVLogger<>("heuristics.csv", new String[]{"time", "candidate", "score"}, e -> new String[]{e.getLocalDateTime().toString(), e.getObservation()
                                                                                                                                                                                                                                 .getSource().toString(), e.getObservation()
                                                                                                                                                                                                                                                           .getHeuristic().toString()});

        beginLaying().source(heuristicsEvents)
                     .pipe(PipeWorks.<TreeHeuristicsEvent>concurrencyBridge())
                     .giveBackgroundThread()
                     .split(lp -> lp.pipe(PipeWorks.asyncBuffer())
                                    .schedule(Duration.ofMillis(100))
                                    .pipe(PipeWorks.unpackingPipe())
                                    .sink(PipeWorks.loggingSink("heuristics", PipeWorks.fileLogger("heuristics")))
                                    .apply())
                     .split(lp -> lp.pipe(PipeWorks.predicatePipe(e -> e instanceof HeuristicComputationEvent))
                                    .pipe(PipeWorks.timer())
                                    .sink(heuristicsLogger)
                                    .schedule(Duration.ofMillis(100))
                                    .apply())
                     .pipe(PipeWorks.predicatePipe(e -> e instanceof TreeHeuristicQueueingEvent))
                     .sink(getMonitor("heuristics.queue.size"))
                     .sink(queueSizeChanges)
                     .schedule(Duration.ofMillis(100))
                     .apply();

    }

}
