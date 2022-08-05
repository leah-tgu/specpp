package org.processmining.estminer.specpp.traits;

public interface DeepCopyable<T extends DeepCopyable<T>> {

    T deepCopy();

}
