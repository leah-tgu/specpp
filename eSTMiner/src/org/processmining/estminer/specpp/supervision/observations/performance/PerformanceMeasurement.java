package org.processmining.estminer.specpp.supervision.observations.performance;

import org.processmining.estminer.specpp.supervision.observations.Statistic;

import java.time.Duration;
import java.time.LocalDateTime;

public class PerformanceMeasurement implements Statistic {
    public LocalDateTime getTime() {
        return time;
    }

    public Duration getDuration() {
        return duration;
    }

    private final LocalDateTime time;
    private final Duration duration;

    public PerformanceMeasurement(LocalDateTime time, Duration duration) {
        this.time = time;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return duration.toString() + " @ " + time.toString();
    }
}
