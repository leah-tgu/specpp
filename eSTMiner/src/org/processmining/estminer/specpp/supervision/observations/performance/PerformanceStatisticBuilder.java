package org.processmining.estminer.specpp.supervision.observations.performance;

import org.processmining.estminer.specpp.datastructures.util.BuilderMap;

public class PerformanceStatisticBuilder extends BuilderMap<TaskDescription, PerformanceStatistic, PerformanceMeasurement> {

    public PerformanceStatisticBuilder() {
        super(PerformanceStatistic::new, PerformanceStatistic::record);
    }

}
