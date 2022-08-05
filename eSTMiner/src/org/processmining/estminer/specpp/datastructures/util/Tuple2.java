package org.processmining.estminer.specpp.datastructures.util;

public class Tuple2<T1, T2> {

    protected final T1 t1;
    protected final T2 t2;

    public Tuple2(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public T1 getT1() {
        return t1;
    }

    public T2 getT2() {
        return t2;
    }
}
