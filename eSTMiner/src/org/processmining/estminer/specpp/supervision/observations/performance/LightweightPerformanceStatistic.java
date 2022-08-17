package org.processmining.estminer.specpp.supervision.observations.performance;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.processmining.estminer.specpp.supervision.observations.Statistic;
import org.processmining.estminer.specpp.supervision.piping.Observer;
import org.processmining.estminer.specpp.traits.Mergeable;

import java.time.Duration;
import java.util.LongSummaryStatistics;

import static org.processmining.estminer.specpp.supervision.observations.performance.PerformanceStatistic.durationToString;

public class LightweightPerformanceStatistic implements PerformanceStatistic {

    private final LongSummaryStatistics nanoStats;


    public LightweightPerformanceStatistic() {
        nanoStats = new LongSummaryStatistics();
    }


    @Override
    public Duration min() {
        return Duration.ofNanos(nanoStats.getMin());
    }

    @Override
    public Duration max() {
        return Duration.ofNanos(nanoStats.getMax());
    }

    @Override
    public Duration avg() {
        return Duration.ofNanos((long) nanoStats.getAverage());
    }

    @Override
    public Duration sum() {
        return Duration.ofNanos(nanoStats.getSum());
    }

    @Override
    public long N() {
        return nanoStats.getCount();
    }

    @Override
    public void merge(Object other) {
        if (other instanceof LightweightPerformanceStatistic) {
            nanoStats.combine(((LightweightPerformanceStatistic) other).nanoStats);
        }
    }

    @Override
    public String toString() {
        return toPrettyString();
    }

    @Override
    public String toPrettyString() {
        Duration sum = sum();
        long n = N();
        int rate = (int) (1e3 * n / (double) sum.toMillis());
        return "{\u03BC=" + durationToString(avg()) + "ms" + " (" + durationToString(min()) + "ms-" + durationToString(max()) + "ms), \u03A3=" + sum.toString()
                                                                                                                                                    .substring(2) + ", N=" + n + ", " + rate + "it/s" + "}";
    }

    public void record(PerformanceMeasurement performanceMeasurement) {
        nanoStats.accept(performanceMeasurement.getDuration().toNanos());
    }

}