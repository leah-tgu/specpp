package org.processmining.estminer.specpp.datastructures.vectorization.spliterators;

import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.util.IndexedItem;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorStorage;

import java.util.Spliterator;
import java.util.function.IntUnaryOperator;

public class IndexedBitMaskSplitty extends AbstractBitMaskSplitty<IndexedItem<Spliterator.OfInt>> {

    private final IntUnaryOperator outsideMapper;


    public IndexedBitMaskSplitty(IntVectorStorage ivs, BitMask bitMask, int startIndex, int limit, IntUnaryOperator outsideMapper) {
        super(ivs, bitMask, startIndex, limit);
        this.outsideMapper = outsideMapper;
    }

    @Override
    protected IndexedItem<OfInt> make(int index) {
        return new IndexedItem<>(outsideMapper.applyAsInt(index), ivs.getVector(index));
    }

    @Override
    protected AbstractBitMaskSplitty<IndexedItem<OfInt>> makePrefix(int oldStart, int half) {
        return new IndexedBitMaskSplitty(ivs, bitMask, oldStart, half, outsideMapper);
    }

}
