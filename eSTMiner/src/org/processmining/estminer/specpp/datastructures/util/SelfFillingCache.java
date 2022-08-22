package org.processmining.estminer.specpp.datastructures.util;

public interface SelfFillingCache<K, V> {

    V get(K key);

}
