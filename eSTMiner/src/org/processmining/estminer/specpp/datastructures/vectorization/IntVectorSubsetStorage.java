package org.processmining.estminer.specpp.datastructures.vectorization;

import org.apache.commons.lang3.ArrayUtils;
import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.IndexSubset;
import org.processmining.estminer.specpp.datastructures.log.OnlyCoversIndexSubset;
import org.processmining.estminer.specpp.datastructures.util.IndexedItem;
import org.processmining.estminer.specpp.datastructures.vectorization.spliterators.BitMaskSplitty;
import org.processmining.estminer.specpp.datastructures.vectorization.spliterators.IndexedBitMaskSplitty;
import org.processmining.estminer.specpp.datastructures.vectorization.spliterators.IndexedSplitty;
import org.processmining.estminer.specpp.datastructures.vectorization.spliterators.Splitty;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

public class IntVectorSubsetStorage extends IntVectorStorage implements OnlyCoversIndexSubset {

    private final IndexSubset indexSubset;

    public IntVectorSubsetStorage(IndexSubset indexSubset, int[] data, int[] startIndices) {
        super(data, startIndices);
        this.indexSubset = indexSubset;
    }

    public static IntVectorSubsetStorage zeros(IndexSubset indexSubset, int[] lengths) {
        int[] startIndices = ArrayUtils.addFirst(lengths, 0);
        Arrays.parallelPrefix(startIndices, Integer::sum);
        return new IntVectorSubsetStorage(indexSubset, new int[startIndices[lengths.length]], startIndices);
    }

    protected int mapIndex(int i) {
        assert isInSubset(i);
        return indexSubset.mapIndex(i);
    }

    protected IntStream mapIndices(IntStream is) {
        return indexSubset.mapIndices(is);
    }

    private boolean isInSubset(int i) {
        return indexSubset.contains(i);
    }

    @Override
    public void setVector(int index, int[] vector) {
        super.setVector(mapIndex(index), vector);
    }

    @Override
    public void setVectorElement(int index, int elementIndex, int value) {
        super.setVectorElement(mapIndex(index), elementIndex, value);
    }

    @Override
    public Spliterator.OfInt getVector(int index) {
        if (!isInSubset(index)) return null;
        return super.getVector(mapIndex(index));
    }

    public Spliterator.OfInt getVectorInternal(int index) {
        return super.getVector(index);
    }

    @Override
    public void mapVector(int index, IntUnaryOperator mapper) {
        super.mapVector(mapIndex(index), mapper);
    }

    @Override
    public IntStream viewVector(int index) {
        return super.viewVector(mapIndex(index));
    }

    @Override
    public IntBuffer vectorBuffer(int index) {
        return super.vectorBuffer(mapIndex(index));
    }

    @Override
    public IntStream indexStream() {
        return indexSubset.streamIndices();
    }

    @Override
    public boolean gt(IntVectorStorage other) {
        if (other instanceof IntVectorSubsetStorage) {
            BitMask overlap = indexSubset.indexIntersection(((IntVectorSubsetStorage) other).indexSubset);
            return IVSComputations.gtOn(mapIndices(overlap.stream()), this, ((IntVectorSubsetStorage) other).mapIndices(overlap.stream()), other);
        } else return super.gt(other);
    }

    @Override
    public boolean lt(IntVectorStorage other) {
        if (other instanceof IntVectorSubsetStorage) {
            BitMask overlap = indexSubset.indexIntersection(((IntVectorSubsetStorage) other).indexSubset);
            return IVSComputations.ltOn(mapIndices(overlap.stream()), this, ((IntVectorSubsetStorage) other).mapIndices(overlap.stream()), other);
        } else return super.gt(other);
    }

    @Override
    public Spliterator<IntBuffer> spliterator() {
        return new BitMaskSplitty(this, indexSubset.getIndices(), 0, indexSubset.getIndexCount());
    }

    @Override
    public Spliterator<IndexedItem<IntBuffer>> indexedSpliterator() {
        return new IndexedBitMaskSplitty(this, indexSubset.getIndices(), 0, indexSubset.getIndexCount(), IntUnaryOperator.identity());
    }

    public Spliterator<IntBuffer> spliterator(BitMask bitMask) {
        assert indexSubset.covers(bitMask);
        return new BitMaskSplitty(this, bitMask, 0, bitMask.cardinality());
    }

    public Spliterator<IndexedItem<IntBuffer>> indexedSpliterator(BitMask bitMask) {
        assert indexSubset.covers(bitMask);
        return new IndexedBitMaskSplitty(this, bitMask, 0, bitMask.cardinality(), IntUnaryOperator.identity());
    }

    @Override
    public IndexSubset getIndexSubset() {
        return indexSubset;
    }

}
