package org.processmining.estminer.specpp.componenting.delegators;

public interface Container<T> {

    void addContent(T content);

    boolean isEmpty();

    boolean hasCapacityLeft();

    default boolean isNonEmpty() {
        return !isEmpty();
    }

}
