package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.Observation;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;

public class ObservationCollection<O extends Observation> implements Collection<O>, Observations<O> {

    private final Collection<O> internal;

    public ObservationCollection(Collection<O> internal) {
        this.internal = internal;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return internal.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return internal.retainAll(c);
    }

    @Override
    public void clear() {
        internal.clear();
    }

    @Override
    public boolean equals(Object o) {
        return internal.equals(o);
    }

    @Override
    public int hashCode() {
        return internal.hashCode();
    }

    @Override
    public Spliterator<O> spliterator() {
        return internal.spliterator();
    }

    @Override
    public int size() {
        return internal.size();
    }

    @Override
    public boolean isEmpty() {
        return internal.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return internal.contains(o);
    }

    @Override
    public Iterator<O> iterator() {
        return internal.iterator();
    }

    @Override
    public Object[] toArray() {
        return internal.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return internal.toArray(a);
    }

    @Override
    public boolean add(O o) {
        return internal.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return internal.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return internal.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends O> c) {
        return internal.addAll(c);
    }

    @Override
    public String toString() {
        return internal.toString();
    }

}
