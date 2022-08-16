package org.processmining.estminer.specpp.datastructures.vectorization.spliterators;

import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorStorage;

import java.nio.IntBuffer;
import java.util.Spliterator;
import java.util.stream.IntStream;

public class Splitty extends AbstractSplitty<IntBuffer> {


    public Splitty(IntVectorStorage intVectorStorage, int startVectorIndex, int fenceVectorIndex) {
        super(intVectorStorage, startVectorIndex, fenceVectorIndex);
    }

    @Override
    protected IntBuffer make(int index) {
        return ivs.vectorBuffer(index);
    }

    @Override
    protected AbstractSplitty<IntBuffer> makePrefix(int low, int mid) {
        return new Splitty(ivs, low, mid);
    }
}
