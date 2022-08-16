package org.processmining.estminer.specpp.supervision.observations.performance;

import org.processmining.estminer.specpp.datastructures.util.BuilderMap;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class LightweightPerformanceStatisticBuilder extends BuilderMap<TaskDescription, PerformanceStatistic, PerformanceMeasurement> {
    public LightweightPerformanceStatisticBuilder() {
        super(LightweightPerformanceStatistic::new, PerformanceStatistic::record);
    }
}
