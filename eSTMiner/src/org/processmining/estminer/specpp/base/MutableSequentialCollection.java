package org.processmining.estminer.specpp.base;

public interface MutableSequentialCollection<T> extends SequentialCollection<T> {

    void remove(T item);

    T removeLast();

}
