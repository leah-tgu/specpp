package org.processmining.estminer.specpp.datastructures.vectorization.spliterators;

import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorStorage;

import java.util.Spliterator;
import java.util.stream.IntStream;

public class BitMaskSplitty extends AbstractBitMaskSplitty<IntStream> {


    public BitMaskSplitty(IntVectorStorage ivs, BitMask bitMask, int startIndex, int limit) {
        super(ivs, bitMask, startIndex, limit);
    }

    @Override
    protected IntStream make(int index) {
        return ivs.viewVector(index);
    }

    @Override
    protected AbstractBitMaskSplitty<IntStream> makePrefix(int oldStart, int half) {
        return new BitMaskSplitty(ivs, bitMask, oldStart, half);
    }
}
