package org.processmining.estminer.specpp.util.datastructures;

import org.processmining.estminer.specpp.traits.ProperlyPrintable;

public class TypedItem<T> extends Tuple2<Class<? extends T>, T> implements ProperlyPrintable {

    public TypedItem(Class<? extends T> tClass, T t) {
        super(tClass, t);
    }

    public Class<? extends T> getType() {
        return t1;
    }

    public T getItem() {
        return t2;
    }

    @Override
    public String toString() {
        return "TypedItem{" + getType().getSimpleName() + ": " + getItem() + '}';
    }

}
