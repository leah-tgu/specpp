package org.processmining.estminer.specpp.util;

import org.processmining.estminer.specpp.datastructures.util.MutableTuple2;
import org.processmining.estminer.specpp.datastructures.util.Tuple2;

import java.util.Spliterator;
import java.util.function.Function;

public class CompatiblePairSpliteratorImpl<T, V> extends CompatibleBiSpliteratorImpl<T, T, V> {
    public CompatiblePairSpliteratorImpl(Spliterator<T> firstSpliterator, Spliterator<T> secondSpliterator, Function<? super Tuple2<T, T>, V> combiner) {
        super(firstSpliterator, secondSpliterator, combiner);
    }

    @Override
    protected MutableTuple2<T, T> createContainer() {
        return new MutablePair<>();
    }

}
