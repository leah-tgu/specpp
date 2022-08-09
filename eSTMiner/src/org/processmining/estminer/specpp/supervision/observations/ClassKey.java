package org.processmining.estminer.specpp.supervision.observations;

public class ClassKey<T> extends StaticHashWrapper<Class<? extends T>> {
    public ClassKey(Class<? extends T> internal) {
        super(internal);
    }

    @Override
    public String toString() {
        return "Class(" + internal.getSimpleName() + ")";
    }
}
