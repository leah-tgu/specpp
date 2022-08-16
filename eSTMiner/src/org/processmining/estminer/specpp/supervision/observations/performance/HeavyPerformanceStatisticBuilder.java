package org.processmining.estminer.specpp.supervision.observations.performance;

import org.processmining.estminer.specpp.datastructures.util.BuilderMap;

public class HeavyPerformanceStatisticBuilder extends BuilderMap<TaskDescription, PerformanceStatistic, PerformanceMeasurement> {

    public HeavyPerformanceStatisticBuilder() {
        super(HeavyPerformanceStatistic::new, PerformanceStatistic::record);
    }

}
