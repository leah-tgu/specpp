package org.processmining.estminer.specpp.datastructures.vectorization.spliterators;

import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.util.IndexedItem;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorStorage;

import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;

public class IndexedBitMaskSplitty extends AbstractBitMaskSplitty<IndexedItem<IntStream>> {

    private final IntUnaryOperator outsideMapper;


    public IndexedBitMaskSplitty(IntVectorStorage ivs, BitMask bitMask, int startIndex, int limit, IntUnaryOperator outsideMapper) {
        super(ivs, bitMask, startIndex, limit);
        this.outsideMapper = outsideMapper;
    }

    @Override
    protected IndexedItem<IntStream> make(int index) {
        return new IndexedItem<>(outsideMapper.applyAsInt(index), ivs.viewVector(index));
    }

    @Override
    protected AbstractBitMaskSplitty<IndexedItem<IntStream>> makePrefix(int oldStart, int half) {
        return new IndexedBitMaskSplitty(ivs, bitMask, oldStart, half, outsideMapper);
    }

}
