package org.processmining.estminer.specpp.representations.encoding;

public interface NonMutatingSetOperations<T> {

    T union(T other);

    T setminus(T other);

    T intersection(T other);

}
