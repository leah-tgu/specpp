package org.processmining.estminer.specpp.datastructures.util;

import org.processmining.estminer.specpp.traits.Immutable;
import org.processmining.estminer.specpp.traits.ProperlyHashable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiPredicate;

public class NoRehashing<T> implements Immutable, ProperlyHashable {

    protected final T internal;
    private final int hash;
    private final BiPredicate<T, Object> equality;

    public NoRehashing(T internal) {
        assert internal != null;
        this.internal = internal;
        boolean isArr = internal.getClass().isArray();

        this.hash = isArr ? Arrays.hashCode((Object[]) internal) : internal.hashCode();
        BiPredicate<T, Object> arrEq = (t1, t2) -> Arrays.equals((Object[]) t1, (Object[]) t2);
        this.equality = isArr ? arrEq : Objects::equals;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoRehashing<?> that = (NoRehashing<?>) o;

        return equality.test(internal, that.internal);
    }

    @Override
    public int hashCode() {
        return hash;
    }


}
