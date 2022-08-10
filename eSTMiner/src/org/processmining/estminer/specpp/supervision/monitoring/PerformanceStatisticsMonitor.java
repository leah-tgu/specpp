package org.processmining.estminer.specpp.supervision.monitoring;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceStatistic;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceStatistics;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class PerformanceStatisticsMonitor implements ComputingMonitor<PerformanceStatistics, PerformanceStatistics, String> {

    private PerformanceStatistics last;

    @Override
    public PerformanceStatistics getMonitoringState() {
        return last;
    }

    @Override
    public void handleObservation(PerformanceStatistics observation) {
        last = observation;
    }

    @Override
    public String computeResult() {
        return last.toPrettyString();
    }
}
