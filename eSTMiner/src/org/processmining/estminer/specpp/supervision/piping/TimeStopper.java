package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceMeasurement;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class TimeStopper extends AbstractAsyncAwareObservable<PerformanceEvent> {

    private final Map<TaskDescription, Long> running;

    public TimeStopper() {
        running = new HashMap<>();
    }


    public void start(TaskDescription taskDescription) {
        running.put(taskDescription, System.currentTimeMillis());
    }

    public void stop(TaskDescription taskDescription) {
        long stop = System.currentTimeMillis();
        long start = running.remove(taskDescription);
        publish(new PerformanceEvent(taskDescription, new PerformanceMeasurement(LocalDateTime.ofInstant(Instant.ofEpochMilli(stop), ZoneId.systemDefault()), Duration.ofMillis(stop - start))));
    }

}
