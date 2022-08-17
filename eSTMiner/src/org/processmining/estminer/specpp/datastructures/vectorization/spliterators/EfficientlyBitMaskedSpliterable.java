package org.processmining.estminer.specpp.datastructures.vectorization.spliterators;

import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.util.IndexedItem;

import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface EfficientlyBitMaskedSpliterable<T> {

    Spliterator<T> efficientSpliterator(BitMask bitMask);

    default Stream<T> efficientStream(BitMask bitMask, boolean parallel) {
        return StreamSupport.stream(efficientSpliterator(bitMask), parallel);
    }

    Spliterator<IndexedItem<T>> efficientIndexedSpliterator(BitMask bitMask);

    default Stream<IndexedItem<T>> efficientIndexedStream(BitMask bitMask, boolean parallel) {
        return StreamSupport.stream(efficientIndexedSpliterator(bitMask), parallel);
    }

}
