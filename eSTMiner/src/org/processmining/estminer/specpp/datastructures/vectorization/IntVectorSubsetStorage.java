package org.processmining.estminer.specpp.datastructures.vectorization;

import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.IndexSubset;
import org.processmining.estminer.specpp.datastructures.log.OnlyCoversIndexSubset;

import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class IntVectorSubsetStorage extends IntVectorStorage implements OnlyCoversIndexSubset {

    private final IndexSubset indexSubset;

    public IntVectorSubsetStorage(IndexSubset indexSubset, int[] data, int[] startIndices) {
        super(data, startIndices);
        this.indexSubset = indexSubset;
    }

    protected int mapIndex(int i) {
        assert isInSubset(i);
        return indexSubset.mapIndex(i);
    }

    protected IntStream mapIndices(IntStream is) {
        return indexSubset.mapIndices(is);
    }

    private boolean isInSubset(int i) {
        return indexSubset.contains(i);
    }

    @Override
    public void setVector(int index, int[] vector) {
        super.setVector(mapIndex(index), vector);
    }

    @Override
    public void setVectorElement(int index, int elementIndex, int value) {
        super.setVectorElement(mapIndex(index), elementIndex, value);
    }

    @Override
    public void mapVector(int index, IntUnaryOperator mapper) {
        super.mapVector(mapIndex(index), mapper);
    }

    @Override
    public void feedInto(IntStream indices, Consumer<IntStream> vectorConsumer) {
        super.feedInto(mapIndices(indices), vectorConsumer);
    }

    @Override
    public IntStream viewVector(int index) {
        return super.viewVector(mapIndex(index));
    }

    @Override
    public IntStream indexStream() {
        return indexSubset.streamIndices();
    }

    @Override
    public Stream<IntStream> view(IntStream indices) {
        return super.view(mapIndices(indices));
    }

    @Override
    public boolean gt(IntVectorStorage other) {
        if (other instanceof IntVectorSubsetStorage) {
            BitMask overlap = indexSubset.indexIntersection(((IntVectorSubsetStorage) other).indexSubset);
            return IVSComputations.gtOn(mapIndices(overlap.stream()), this, ((IntVectorSubsetStorage) other).mapIndices(overlap.stream()), other);
        } else return super.gt(other);
    }

    @Override
    public boolean lt(IntVectorStorage other) {
        if (other instanceof IntVectorSubsetStorage) {
            BitMask overlap = indexSubset.indexIntersection(((IntVectorSubsetStorage) other).indexSubset);
            return IVSComputations.ltOn(mapIndices(overlap.stream()), this, ((IntVectorSubsetStorage) other).mapIndices(overlap.stream()), other);
        } else return super.gt(other);
    }


    @Override
    public IndexSubset getIndexSubset() {
        return indexSubset;
    }

}
