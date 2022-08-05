package org.processmining.estminer.specpp.datastructures.util;

import com.google.common.collect.ImmutableList;

import java.util.Iterator;

public class Pair<T> extends Tuple2<T, T> implements Iterable<T> {


    public Pair(T t1, T t2) {
        super(t1, t2);
    }

    public T first() {
        return t1;
    }

    public T second() {
        return t2;
    }

    @Override
    public Iterator<T> iterator() {
        return ImmutableList.of(t1, t2).iterator();
    }
}
