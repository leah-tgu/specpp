package org.processmining.estminer.specpp.componenting.delegators;

public interface Delegator<T> extends Container<T> {

    void setDelegate(T delegate);

    T getDelegate();

    boolean isSet();

    @Override
    default void addContent(T content) {
        setDelegate(content);
    }

    @Override
    default boolean hasCapacityLeft() {
        return !isSet();
    }

    @Override
    default boolean isEmpty() {
        return !isSet();
    }

}
