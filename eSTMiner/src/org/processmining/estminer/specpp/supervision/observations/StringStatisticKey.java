package org.processmining.estminer.specpp.supervision.observations;

import org.processmining.estminer.specpp.datastructures.util.NoRehashing;

public class StringStatisticKey extends NoRehashing<String> implements StatisticKey, Comparable<StringStatisticKey> {


    public StringStatisticKey(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return internal;
    }

    @Override
    public int compareTo(StringStatisticKey o) {
        return internal.compareTo(o.internal);
    }
}
