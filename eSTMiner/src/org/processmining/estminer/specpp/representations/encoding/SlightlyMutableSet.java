package org.processmining.estminer.specpp.representations.encoding;

public interface SlightlyMutableSet<T> {

    boolean contains(T item);

    boolean add(T item);

    boolean remove(T item);

    default void addAll(T... items) {
        for (T item : items) {
            add(item);
        }
    }

    default int size() {
        return cardinality();
    }

    int cardinality();

    boolean isEmpty();

    void clear();

}
