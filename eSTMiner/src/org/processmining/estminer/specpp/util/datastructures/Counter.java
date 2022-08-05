package org.processmining.estminer.specpp.util.datastructures;

import java.util.HashMap;

public class Counter<T> extends HashMap<T, Integer> {

    public void inc(T t, int by) {
        put(t, getOrDefault(t, 0) + by);
    }

    public void inc(T t) {
        inc(t, 1);
    }

}
