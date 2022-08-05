package org.processmining.estminer.specpp.supervision.observations;

public class ClassStatisticKey<T> extends WrappingStatisticKey<Class<? extends T>> {
    public ClassStatisticKey(Class<? extends T> internal) {
        super(internal);
    }

    @Override
    public String toString() {
        return "Class(" + internal.getSimpleName() + ")";
    }
}
