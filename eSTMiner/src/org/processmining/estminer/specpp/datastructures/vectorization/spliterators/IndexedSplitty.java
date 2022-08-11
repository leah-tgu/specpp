package org.processmining.estminer.specpp.datastructures.vectorization.spliterators;

import org.processmining.estminer.specpp.datastructures.util.IndexedItem;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorStorage;

import java.util.Spliterator;
import java.util.function.IntUnaryOperator;

public class IndexedSplitty extends AbstractSplitty<IndexedItem<Spliterator.OfInt>> {
    private final IntUnaryOperator outsideMapper;

    public IndexedSplitty(IntVectorStorage ivs, int startVectorIndex, int fenceVectorIndex, IntUnaryOperator outsideMapper) {
        super(ivs, startVectorIndex, fenceVectorIndex);
        this.outsideMapper = outsideMapper;
    }

    @Override
    protected IndexedItem<OfInt> make(int index) {
        return new IndexedItem<>(outsideMapper.applyAsInt(index), ivs.getVector(index));
    }

    @Override
    protected AbstractSplitty<IndexedItem<OfInt>> makePrefix(int low, int mid) {
        return new IndexedSplitty(ivs, low, mid, outsideMapper);
    }
}
