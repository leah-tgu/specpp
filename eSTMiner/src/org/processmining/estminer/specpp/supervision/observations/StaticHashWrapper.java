package org.processmining.estminer.specpp.supervision.observations;

import org.processmining.estminer.specpp.datastructures.util.NoRehashing;

public class StaticHashWrapper<T> extends NoRehashing<T> implements StatisticKey {

    public StaticHashWrapper(T internal) {
        super(internal);
    }

    @Override
    public String toString() {
        return "WrappedKey(" + internal + ")";
    }

}
