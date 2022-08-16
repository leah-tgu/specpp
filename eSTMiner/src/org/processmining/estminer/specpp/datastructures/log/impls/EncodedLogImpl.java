package org.processmining.estminer.specpp.datastructures.log.impls;

import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncoding;
import org.processmining.estminer.specpp.datastructures.log.Activity;
import org.processmining.estminer.specpp.datastructures.util.IndexedItem;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVector;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorStorage;

import java.nio.IntBuffer;
import java.util.Spliterator;
import java.util.stream.IntStream;

public class EncodedLogImpl implements EncodedLog {
    private final IntVector variantFrequencies;
    protected final IntEncoding<Activity> encoding;
    protected final IntVectorStorage ivs;

    public EncodedLogImpl(IntVector variantFrequencies, IntVectorStorage ivs, IntEncoding<Activity> encoding) {
        assert variantFrequencies.length() == ivs.getVectorCount();
        this.variantFrequencies = variantFrequencies;
        this.encoding = encoding;
        this.ivs = ivs;
    }

    @Override
    public IntEncoding<Activity> getEncoding() {
        return encoding;
    }

    @Override
    public IntVectorStorage getEncodedVariantVectors() {
        return ivs;
    }

    @Override
    public IntVector getVariantFrequencies() {
        return variantFrequencies;
    }

    @Override
    public int getVariantFrequency(int index) {
        return variantFrequencies.get(index);
    }

    @Override
    public IntStream getEncodedVariant(int index) {
        return ivs.viewVector(index);
    }

    @Override
    public IntStream streamIndices() {
        return ivs.indexStream();
    }

    @Override
    public BitMask variantIndices() {
        return BitMask.of(streamIndices());
    }

    @Override
    public int variantCount() {
        return ivs.getVectorCount();
    }

    @Override
    public int totalTraceCount() {
        return variantFrequencies.sum();
    }

    @Override
    public Spliterator<IntBuffer> efficientSpliterator() {
        return ivs.spliterator();
    }

    @Override
    public Spliterator<IndexedItem<IntBuffer>> efficientIndexedSpliterator() {
        return ivs.indexedSpliterator();
    }
}
