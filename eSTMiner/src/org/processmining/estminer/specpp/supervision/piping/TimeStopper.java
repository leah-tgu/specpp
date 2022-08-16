package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceMeasurement;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.supervision.observations.performance.TimedPerformanceMeasurement;

import java.time.*;
import java.util.HashMap;
import java.util.Map;

public class TimeStopper extends AbstractAsyncAwareObservable<PerformanceEvent> {

    private final Map<TaskDescription, Long> running;
    private ZoneOffset zoneOffset = ZoneOffset.systemDefault().getRules().getStandardOffset(Instant.now()); // incorrect

    public TimeStopper() {
        running = new HashMap<>();
    }


    public void start(TaskDescription taskDescription) {
        running.put(taskDescription, System.currentTimeMillis());
    }

    public void stop(TaskDescription taskDescription) {
        long stop = System.currentTimeMillis();
        long start = running.remove(taskDescription);
        publish(new PerformanceEvent(taskDescription, new PerformanceMeasurement(Duration.ofMillis(stop - start))));
    }

}
