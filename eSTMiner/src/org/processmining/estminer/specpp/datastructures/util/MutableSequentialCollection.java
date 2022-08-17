package org.processmining.estminer.specpp.datastructures.util;

import org.processmining.estminer.specpp.datastructures.util.SequentialCollection;

public interface MutableSequentialCollection<T> extends SequentialCollection<T> {

    void remove(T item);

    T removeLast();

}
