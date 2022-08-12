package org.processmining.estminer.specpp.datastructures.vectorization.spliterators;

import org.processmining.estminer.specpp.datastructures.util.IndexedItem;

import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface EfficientlySpliterable<T> {

    Spliterator<T> efficientSpliterator();

    default Stream<T> efficientStream(boolean parallel) {
        return StreamSupport.stream(efficientSpliterator(), parallel);
    }

    Spliterator<IndexedItem<T>> efficientIndexedSpliterator();

    default Stream<IndexedItem<T>> efficientIndexedStream(boolean parallel) {
        return StreamSupport.stream(efficientIndexedSpliterator(), parallel);
    }

}
