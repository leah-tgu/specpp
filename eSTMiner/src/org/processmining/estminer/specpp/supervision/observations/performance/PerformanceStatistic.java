package org.processmining.estminer.specpp.supervision.observations.performance;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.processmining.estminer.specpp.supervision.observations.Statistic;
import org.processmining.estminer.specpp.traits.Mergeable;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class PerformanceStatistic implements Statistic, Mergeable {

    private final List<PerformanceMeasurement> measurements;
    private final SummaryStatistics stats;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.###");

    public PerformanceStatistic() {
        measurements = new ArrayList<>();
        stats = new SummaryStatistics();
    }

    public void record(PerformanceMeasurement measurement) {
        measurements.add(measurement);
        stats.addValue(measurement.getDuration().toNanos());
    }

    public SummaryStatistics milliStatistics() {
        return stats;
    }

    public Duration min() {
        return Duration.ofNanos((long) stats.getMin());
    }

    public Duration max() {
        return Duration.ofNanos((long) stats.getMax());
    }

    public Duration avg() {
        return Duration.ofNanos((long) stats.getMean());
    }

    public Duration sum() {
        return Duration.ofNanos((long) stats.getSum());
    }

    public Duration std() {
        return Duration.ofNanos((long) stats.getStandardDeviation());
    }

    public long N() {
        return stats.getN();
    }

    private String durationToString(Duration duration) {
        return decimalFormat.format((double) duration.toNanos() / 1e6);
    }

    @Override
    public String toString() {
        return "{\u03BC=" + durationToString(avg()) + "ms\u00B1" + durationToString(std()) + "ms" + " (" + durationToString(min()) + "ms-" + durationToString(max()) + "ms), \u03A3=" + sum().toString()
                                                                                                                                                                                            .substring(2) + ", N=" + N() + "}";
    }

    @Override
    public void merge(Object other) {
        if (other instanceof PerformanceStatistic) {
            for (PerformanceMeasurement m : ((PerformanceStatistic) other).measurements) {
                record(m);
            }
        }
    }

}
