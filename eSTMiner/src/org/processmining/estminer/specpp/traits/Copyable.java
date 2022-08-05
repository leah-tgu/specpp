package org.processmining.estminer.specpp.traits;

public interface Copyable<T extends Copyable<T>> {

    T copy();

}
