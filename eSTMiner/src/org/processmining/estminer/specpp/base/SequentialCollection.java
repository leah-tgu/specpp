package org.processmining.estminer.specpp.base;

import java.util.Set;
import java.util.function.Consumer;

public interface SequentialCollection<T> extends Consumer<T>, Iterable<T> {

    int size();

    boolean hasCapacityLeft();

    Set<T> toSet();

}
