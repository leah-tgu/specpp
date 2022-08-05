package org.processmining.estminer.specpp.representations.encoding;

public interface PrimitiveIntSet extends SlightlyMutableSet<Integer> {

    boolean containsInt(int item);

    @Override
    default boolean contains(Integer item) {
        return contains(item);
    }

    boolean containsIndex(int index);

    boolean addInt(int item);

    @Override
    default boolean add(Integer item) {
        return addInt(item);
    }

    boolean removeInt(int item);

    @Override
    default boolean remove(Integer item) {
        return removeInt(item);
    }
}
