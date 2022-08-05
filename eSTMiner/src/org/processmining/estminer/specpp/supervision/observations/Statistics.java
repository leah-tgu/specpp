package org.processmining.estminer.specpp.supervision.observations;

import org.processmining.estminer.specpp.traits.Mergeable;
import org.processmining.estminer.specpp.traits.ProperlyPrintable;

import java.util.HashMap;
import java.util.Map;

public class Statistics<K extends StatisticKey, S extends Statistic> implements Observation, Mergeable, ProperlyPrintable {

    private final Map<K, S> internal;

    public Statistics() {
        internal = new HashMap<>();
    }

    public Statistics(Map<K, S> input) {
        internal = new HashMap<>(input);
    }

    public void record(K key, S statistic) {
        internal.put(key, statistic);
    }

    @Override
    public String toString() {
        return internal.toString();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void merge(Object other) {
        if (other instanceof Statistics) {
            for (Map.Entry<StatisticKey, Statistic> entry : ((Statistics<StatisticKey, Statistic>) other).internal.entrySet()) {
                K key = (K) entry.getKey();
                S value = (S) entry.getValue();
                if (!internal.containsKey(key)) record(key, value);
                else {
                    Statistic statistic = internal.get(key);
                    if (statistic instanceof Mergeable) ((Mergeable) statistic).merge(value);
                }
            }
        }
    }
}
