package org.processmining.estminer.specpp.datastructures.vectorization.spliterators;

import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorStorage;

import java.util.Spliterator;

public class BitMaskSplitty extends AbstractBitMaskSplitty<Spliterator.OfInt> {


    public BitMaskSplitty(IntVectorStorage ivs, BitMask bitMask, int startIndex, int limit) {
        super(ivs, bitMask, startIndex, limit);
    }

    @Override
    protected OfInt make(int index) {
        return ivs.getVector(index);
    }

    @Override
    protected AbstractBitMaskSplitty<OfInt> makePrefix(int oldStart, int half) {
        return new BitMaskSplitty(ivs, bitMask, oldStart, half);
    }
}
