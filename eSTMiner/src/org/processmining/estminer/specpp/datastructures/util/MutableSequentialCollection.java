package org.processmining.estminer.specpp.datastructures.util;

public interface MutableSequentialCollection<T> extends SequentialCollection<T> {

    void remove(T item);

    T removeLast();

}
