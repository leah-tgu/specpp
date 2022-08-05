package org.processmining.estminer.specpp.supervision.supervisors;

import org.processmining.estminer.specpp.componenting.delegators.DelegatingAdHocObservable;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingObservable;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.est.PlaceNode;
import org.processmining.estminer.specpp.representations.tree.events.LeafEvent;
import org.processmining.estminer.specpp.supervision.CSVLogger;
import org.processmining.estminer.specpp.supervision.monitoring.TimeSeriesMonitor;
import org.processmining.estminer.specpp.supervision.observations.EventCountStatistics;
import org.processmining.estminer.specpp.supervision.observations.TreeEvent;
import org.processmining.estminer.specpp.supervision.observations.TreeStatsEvent;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;

import java.time.Duration;
import java.time.LocalDateTime;

public class DetailedTreeSupervisor extends MonitoringSupervisor {

    private final DelegatingObservable<TreeEvent> treeEvents = new DelegatingObservable<>();
    private final DelegatingObservable<EventCountStatistics> treeCounts = new DelegatingObservable<>();
    private final DelegatingAdHocObservable<TreeStatsEvent> treeStats = new DelegatingAdHocObservable<>();

    public DetailedTreeSupervisor() {
        componentSystemAdapter().require(SupervisionRequirements.observable("tree.events", TreeEvent.class), treeEvents)
                                .require(SupervisionRequirements.adHocObservable("tree.stats", TreeStatsEvent.class), treeStats)
                                .require(SupervisionRequirements.observable("tree.events.count", EventCountStatistics.class), treeCounts);
        createMonitor("tree.leaves.count", new TimeSeriesMonitor<>("leaves.count", TimeSeriesMonitor.<LeafEvent<PlaceNode>>delta_accumulator()));
    }

    @Override
    protected void instantiateObservationHandling() {
        beginLaying().source(treeCounts)
                     .pipe(PipeWorks.accumulatingPipe(EventCountStatistics::new))
                     .<EventCountStatistics>export(p -> componentSystemAdapter().provide(SupervisionRequirements.observable("tree.events.accumulation", EventCountStatistics.class, p)))
                     .sink(PipeWorks.loggingSink(consoleLogger, "tree.events.accumulation"))
                     .sink(PipeWorks.loggingSink(fileLogger, "tree.events.accumulation"))
                     .apply();

        CSVLogger<LeafEvent<PlaceNode>> leafCountChanges = new CSVLogger<>("tree.csv", new String[]{"time", "place", "change", "tree.leaves.count delta"}, e -> new String[]{LocalDateTime.now().toString(), e.getSource()
                                                                                                                                                                                                              .getProperties().toString(), e.getClass().getSimpleName(), Integer.toString(e.getDelta())});


        beginLaying().source(treeEvents)
                     .pipe(PipeWorks.concurrencyBridge())
                     .giveBackgroundThread()
                     .pipe(PipeWorks.predicatePipe(e -> e instanceof LeafEvent))
                     .sink(getMonitor("tree.leaves.count"))
                     .sink(leafCountChanges)
                     .schedule(Duration.ofMillis(100))
                     .apply();

    }


}
