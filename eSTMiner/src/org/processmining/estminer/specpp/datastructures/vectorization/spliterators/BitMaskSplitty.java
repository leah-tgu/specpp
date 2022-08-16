package org.processmining.estminer.specpp.datastructures.vectorization.spliterators;

import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorStorage;

import java.nio.IntBuffer;
import java.util.Spliterator;
import java.util.stream.IntStream;

public class BitMaskSplitty extends AbstractBitMaskSplitty<IntBuffer> {


    public BitMaskSplitty(IntVectorStorage ivs, BitMask bitMask, int startIndex, int limit) {
        super(ivs, bitMask, startIndex, limit);
    }

    @Override
    protected IntBuffer make(int index) {
        return ivs.vectorBuffer(index);
    }

    @Override
    protected AbstractBitMaskSplitty<IntBuffer> makePrefix(int oldStart, int half) {
        return new BitMaskSplitty(ivs, bitMask, oldStart, half);
    }
}
