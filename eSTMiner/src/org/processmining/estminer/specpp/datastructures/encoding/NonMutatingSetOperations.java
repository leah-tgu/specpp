package org.processmining.estminer.specpp.datastructures.encoding;

/**
 * Specifies non-mutating, i.e. copying, set operations.
 * @param <T>
 */
public interface NonMutatingSetOperations<T> {

    T union(T other);

    T setminus(T other);

    T intersection(T other);

}
