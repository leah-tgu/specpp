package org.processmining.estminer.specpp.supervision.observations.performance;

import org.processmining.estminer.specpp.supervision.observations.Statistics;

import java.util.Map;

public class PerformanceStatistics extends Statistics<TaskDescription, PerformanceStatistic> {

    public PerformanceStatistics() {
    }

    public PerformanceStatistics(Map<TaskDescription, PerformanceStatistic> input) {
        super(input);
    }

    @Override
    public String toString() {
        return "PerformanceStatistics:" + super.toString();
    }
}
