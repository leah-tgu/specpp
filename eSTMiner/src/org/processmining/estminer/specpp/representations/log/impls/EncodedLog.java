package org.processmining.estminer.specpp.representations.log.impls;

import com.google.common.collect.Streams;
import org.processmining.estminer.specpp.representations.BitMask;
import org.processmining.estminer.specpp.representations.encoding.IntEncoding;
import org.processmining.estminer.specpp.representations.log.Activity;
import org.processmining.estminer.specpp.representations.vectorization.IntVectorStorage;
import org.processmining.estminer.specpp.traits.Streamable;
import org.processmining.estminer.specpp.util.datastructures.IndexedItem;

import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EncodedLog implements Streamable<IndexedItem<IntStream>>, Iterable<IndexedItem<IntStream>> {
    protected final IntEncoding<Activity> encoding;
    protected final IntVectorStorage ivs;

    public EncodedLog(IntVectorStorage ivs, IntEncoding<Activity> encoding) {
        this.encoding = encoding;
        this.ivs = ivs;
    }

    protected EncodedLog(int[] data, int[] startIndices, IntEncoding<Activity> encoding) {
        this(new IntVectorStorage(data, startIndices), encoding);
    }

    public IntEncoding<Activity> getEncoding() {
        return encoding;
    }

    public IntVectorStorage getEncodedVariantVectors() {
        return ivs;
    }

    public IntStream streamIndices() {
        return ivs.indexStream();
    }

    public BitMask variantIndices() {
        return BitMask.of(streamIndices());
    }

    public int getVariantCount() {
        return ivs.getVectorCount();
    }

    @Override
    public Stream<IndexedItem<IntStream>> stream() {
        return Streams.zip(streamIndices().boxed(), ivs.view(), IndexedItem::new);
    }

    @Override
    public Iterator<IndexedItem<IntStream>> iterator() {
        return stream().iterator();
    }
}
