package org.processmining.estminer.specpp.supervision.observations.performance;

import org.processmining.estminer.specpp.datastructures.util.BuilderMap;

public class LightweightPerformanceStatisticBuilder extends BuilderMap<TaskDescription, PerformanceStatistic, PerformanceMeasurement> {
    public LightweightPerformanceStatisticBuilder() {
        super(LightweightPerformanceStatistic::new, PerformanceStatistic::record);
    }
}
