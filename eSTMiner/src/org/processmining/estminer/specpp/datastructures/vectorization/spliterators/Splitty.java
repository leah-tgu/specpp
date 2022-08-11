package org.processmining.estminer.specpp.datastructures.vectorization.spliterators;

import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorStorage;

import java.util.Spliterator;

public class Splitty extends AbstractSplitty<Spliterator.OfInt> {


    public Splitty(IntVectorStorage intVectorStorage, int startVectorIndex, int fenceVectorIndex) {
        super(intVectorStorage, startVectorIndex, fenceVectorIndex);
    }

    @Override
    protected OfInt make(int index) {
        return ivs.getVector(index);
    }

    @Override
    protected AbstractSplitty<OfInt> makePrefix(int low, int mid) {
        return new Splitty(ivs, low, mid);
    }
}
