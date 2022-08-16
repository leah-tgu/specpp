package org.processmining.estminer.specpp.datastructures.encoding;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Sets;
import org.processmining.estminer.specpp.traits.Copyable;
import org.processmining.estminer.specpp.traits.ProperlyHashable;
import org.processmining.estminer.specpp.traits.ProperlyPrintable;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents an {@code IntEncoding<T>} with an internal HashMap.
 *
 * @param <T> domain of this encoding
 */
public class HashmapEncoding<T> implements IntEncoding<T>, ProperlyHashable, Copyable<HashmapEncoding<T>>, ProperlyPrintable {

    protected final BiMap<T, Integer> internal;
    protected final int size;

    public HashmapEncoding(Set<T> items, Comparator<T> ordering) {
        ArrayList<T> list = new ArrayList<>(items);
        list.sort(ordering);
        ImmutableBiMap.Builder<T, Integer> builder = ImmutableBiMap.builder();
        for (int i = 0; i < list.size(); i++) {
            builder.put(list.get(i), i);
        }
        internal = builder.build();
        size = internal.size();
        assertInvariant();
    }


    public HashmapEncoding(List<T> orderedDistinctItems) {
        ImmutableBiMap.Builder<T, Integer> builder = ImmutableBiMap.builder();
        for (int i = 0; i < orderedDistinctItems.size(); i++) {
            builder.put(orderedDistinctItems.get(i), i);
        }
        internal = builder.build();
        size = internal.size();
        assertInvariant();
    }

    protected HashmapEncoding(ImmutableBiMap<T, Integer> indices) {
        this.internal = indices;
        this.size = internal.size();
        assertInvariant();
    }

    protected void assertInvariant() {
        assert size == internal.values().stream().max(Comparator.naturalOrder()).get() + 1;
    }

    public static <T> HashmapEncoding<T> copyOf(Map<T, Integer> map) {
        return new HashmapEncoding<>(ImmutableBiMap.copyOf(map));
    }

    protected boolean isValid() {
        TreeSet<Integer> integers = Sets.newTreeSet(internal.values());
        int k = 0;
        for (Integer i : integers) {
            if (i != k++) return false;
        }
        return k >= internal.size();
    }

    public int size() {
        return size;
    }

    @Override
    public Stream<T> domain() {
        return internal.keySet().stream();
    }

    @Override
    public Stream<Integer> range() {
        return primitiveRange().boxed();
    }

    @Override
    public IntStream primitiveRange() {
        return IntStream.range(0, size);
    }

    @Override
    public boolean isInDomain(T toEncode) {
        return internal.containsKey(toEncode);
    }

    @Override
    public boolean isInRange(Integer toDecode) {
        return isIntInRange(toDecode);
    }

    @Override
    public boolean isIntInRange(int toDecode) {
        return toDecode < size && toDecode >= 0;
    }

    @Override
    public Integer encode(T item) {
        return internal.get(item);
    }

    @Override
    public T decode(Integer index) {
        return internal.inverse().get(index);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HashmapEncoding<?> encoding = (HashmapEncoding<?>) o;

        return internal.equals(encoding.internal);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        Integer[] ix = internal.values().toArray(new Integer[0]);
        for (int i = 0; i < ix.length; i++) {
            sb.append(decode(ix[i]).toString()).append(" : ").append(ix[i]);
            if (i < ix.length - 1) sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return internal.hashCode();
    }

    @Override
    public HashmapEncoding<T> copy() {
        return new HashmapEncoding<>(ImmutableBiMap.copyOf(internal));
    }
}
