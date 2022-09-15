package org.processmining.specpp.datastructures.encoding;

import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.traits.Copyable;

/**
 * Specifies mutating set operations on sets.
 * The static definitions allow non mutating application of these methods on copyable implementations.
 *
 * @param <T>
 */
public interface MutatingSetOperations<T extends MutatingSetOperations<T>> {


    void union(T other);

    void setminus(T other);

    void intersection(T other);

}
