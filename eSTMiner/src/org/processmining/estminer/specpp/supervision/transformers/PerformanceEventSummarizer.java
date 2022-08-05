package org.processmining.estminer.specpp.supervision.transformers;

import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceStatisticBuilder;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceStatistics;
import org.processmining.estminer.specpp.supervision.piping.ObservationSummarizer;
import org.processmining.estminer.specpp.supervision.piping.Observations;

public class PerformanceEventSummarizer implements ObservationSummarizer<PerformanceEvent, PerformanceStatistics> {

    @Override
    public PerformanceStatistics summarize(Observations<? extends PerformanceEvent> events) {
        PerformanceStatisticBuilder collector = new PerformanceStatisticBuilder();
        for (PerformanceEvent event : events) {
            collector.add(event.getTask(), event.getMeasurement());
        }
        return new PerformanceStatistics(collector.getMap());
    }

}
