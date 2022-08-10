package org.processmining.estminer.specpp.supervision.observations;

import org.processmining.estminer.specpp.traits.Mergeable;
import org.processmining.estminer.specpp.traits.PrettyPrintable;
import org.processmining.estminer.specpp.traits.ProperlyPrintable;

import java.util.*;

public class Statistics<K extends StatisticKey, S extends Statistic> implements Observation, Mergeable, ProperlyPrintable, PrettyPrintable {

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

    public Set<Map.Entry<K, S>> getRecords() {
        return internal.entrySet();
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

    @Override
    public String toPrettyString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Statistics:").append(" {").append("\n");
        ArrayList<Map.Entry<K, S>> entries = new ArrayList<>(getRecords());
        entries.sort(Comparator.comparing(e -> e.getKey().toString()));
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<K, S> record = entries.get(i);
            sb.append("\t").append(record);
            if (i < entries.size() - 1) sb.append("\n");
        }
        return sb.append("}").toString();
    }

}
