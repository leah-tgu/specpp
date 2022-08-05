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

public class HashmapEncoding<T> implements IntEncoding<T>, ProperlyHashable, Copyable<HashmapEncoding<T>>, ProperlyPrintable {

    protected final BiMap<T, Integer> internal;

    public HashmapEncoding(Set<T> items, Comparator<T> ordering) {
        ArrayList<T> list = new ArrayList<>(items);
        list.sort(ordering);
        ImmutableBiMap.Builder<T, Integer> builder = ImmutableBiMap.builder();
        for (int i = 0; i < list.size(); i++) {
            builder.put(list.get(i), i);
        }
        internal = builder.build();
    }

    public HashmapEncoding(List<T> items) {
        ImmutableBiMap.Builder<T, Integer> builder = ImmutableBiMap.builder();
        for (int i = 0; i < items.size(); i++) {
            builder.put(items.get(i), i);
        }
        internal = builder.build();
    }

    protected HashmapEncoding(ImmutableBiMap<T, Integer> indices) {
        this.internal = indices;
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
        return internal.size();
    }

    @Override
    public Stream<T> domain() {
        return internal.keySet().stream();
    }

    @Override
    public Stream<Integer> range() {
        return internal.values().stream(); // IteratorUtils.asIterable(IntStream.range(0, size()).boxed().iterator());
    }

    @Override
    public IntStream primitiveRange() {
        return range().mapToInt(i -> i);
    }

    @Override
    public boolean isInDomain(T toEncode) {
        return internal.containsKey(toEncode);
    }

    @Override
    public boolean isInRange(Integer toDecode) {
        return internal.containsValue(toDecode);
    }

    @Override
    public boolean isIntInRange(int toDecode) {
        return internal.containsValue(toDecode);
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
