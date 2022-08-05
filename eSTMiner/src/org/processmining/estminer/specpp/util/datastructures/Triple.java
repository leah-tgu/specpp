package org.processmining.estminer.specpp.util.datastructures;

import org.apache.commons.collections4.IteratorUtils;

import java.util.Iterator;

public class Triple<T> extends Tuple3<T, T, T> implements Iterable<T> {
    public Triple(T t, T t2, T t3) {
        super(t, t2, t3);
    }

    public T first() {
        return t1;
    }

    public T second() {
        return t2;
    }

    public T third() {
        return t3;
    }

    @Override
    public Iterator<T> iterator() {
        return IteratorUtils.arrayIterator(t1, t2, t3);
    }

}
