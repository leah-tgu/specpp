package org.processmining.estminer.specpp.supervision.monitoring;

import org.processmining.estminer.specpp.supervision.observations.ClassStatisticKey;
import org.processmining.estminer.specpp.supervision.observations.Observation;

import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.function.ToIntFunction;

public class IntSummaryStatisticsMonitorMap<O extends Observation> implements Monitor<O, Map<ClassStatisticKey<? extends Observation>, IntSummaryStatistics>> {

    private final Map<ClassStatisticKey<? extends Observation>, IntSummaryStatistics> summaryStatisticsMap;
    private final ToIntFunction<O> intExtractor;

    public IntSummaryStatisticsMonitorMap(ToIntFunction<O> intExtractor) {
        this.intExtractor = intExtractor;
        summaryStatisticsMap = new HashMap<>();
    }

    @Override
    public Map<ClassStatisticKey<? extends Observation>, IntSummaryStatistics> getMonitoringState() {
        return summaryStatisticsMap;
    }

    @Override
    public void handleObservation(O observation) {
        ClassStatisticKey<? extends Observation> key = new ClassStatisticKey<>(observation.getClass());
        if (!summaryStatisticsMap.containsKey(key)) summaryStatisticsMap.put(key, new IntSummaryStatistics());
        summaryStatisticsMap.get(key).accept(intExtractor.applyAsInt(observation));
    }


}
