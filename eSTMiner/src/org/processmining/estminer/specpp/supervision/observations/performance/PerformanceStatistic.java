package org.processmining.estminer.specpp.supervision.observations.performance;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.processmining.estminer.specpp.supervision.observations.Statistic;
import org.processmining.estminer.specpp.supervision.piping.Observer;
import org.processmining.estminer.specpp.traits.Mergeable;
import org.processmining.estminer.specpp.traits.PrettyPrintable;

import java.text.DecimalFormat;
import java.time.Duration;

public interface PerformanceStatistic extends Statistic, Mergeable, Observer<PerformanceMeasurement>, PrettyPrintable {
    DecimalFormat decimalFormat = new DecimalFormat("0.###");

    static String durationToString(Duration duration) {
        return decimalFormat.format((double) duration.toNanos() / 1e6);
    }

    void record(PerformanceMeasurement measurement);

    Duration min();

    Duration max();

    Duration avg();

    Duration sum();

    long N();

    @Override
    void merge(Object other);

    @Override
    String toPrettyString();

    @Override
    default void observe(PerformanceMeasurement observation) {
        record(observation);
    }
}
