package org.processmining.estminer.specpp.datastructures.vectorization.spliterators;

import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.util.IndexedItem;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorStorage;

import java.nio.IntBuffer;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

public class IndexedBitMaskSplitty extends AbstractBitMaskSplitty<IndexedItem<IntBuffer>> {

    private final IntUnaryOperator outsideMapper;


    public IndexedBitMaskSplitty(IntVectorStorage ivs, BitMask bitMask, int startIndex, int limit, IntUnaryOperator outsideMapper) {
        super(ivs, bitMask, startIndex, limit);
        this.outsideMapper = outsideMapper;
    }

    @Override
    protected IndexedItem<IntBuffer> make(int index) {
        return new IndexedItem<>(outsideMapper.applyAsInt(index), ivs.vectorBuffer(index));
    }

    @Override
    protected AbstractBitMaskSplitty<IndexedItem<IntBuffer>> makePrefix(int oldStart, int half) {
        return new IndexedBitMaskSplitty(ivs, bitMask, oldStart, half, outsideMapper);
    }

}
