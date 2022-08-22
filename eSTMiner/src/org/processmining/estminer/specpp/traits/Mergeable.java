package org.processmining.estminer.specpp.traits;

public interface Mergeable<T> extends Mutable {

    void merge(T other);

}
