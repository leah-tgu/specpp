package org.processmining.estminer.specpp.supervision.observations;

import org.processmining.estminer.specpp.datastructures.util.NoRehashing;

public class WrappingStatisticKey<T> extends NoRehashing<T> implements StatisticKey {

    public WrappingStatisticKey(T internal) {
        super(internal);
    }

    @Override
    public String toString() {
        return "WrappedKey(" + internal + ")";
    }

}
