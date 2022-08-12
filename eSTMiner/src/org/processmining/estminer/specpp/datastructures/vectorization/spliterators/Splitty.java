package org.processmining.estminer.specpp.datastructures.vectorization.spliterators;

import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorStorage;

import java.util.Spliterator;
import java.util.stream.IntStream;

public class Splitty extends AbstractSplitty<IntStream> {


    public Splitty(IntVectorStorage intVectorStorage, int startVectorIndex, int fenceVectorIndex) {
        super(intVectorStorage, startVectorIndex, fenceVectorIndex);
    }

    @Override
    protected IntStream make(int index) {
        return ivs.viewVector(index);
    }

    @Override
    protected AbstractSplitty<IntStream> makePrefix(int low, int mid) {
        return new Splitty(ivs, low, mid);
    }
}
