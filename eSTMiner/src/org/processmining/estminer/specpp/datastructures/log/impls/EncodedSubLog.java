package org.processmining.estminer.specpp.datastructures.log.impls;

import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.IndexSubset;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncoding;
import org.processmining.estminer.specpp.datastructures.log.Activity;
import org.processmining.estminer.specpp.datastructures.log.OnlyCoversIndexSubset;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorSubsetStorage;

import java.util.stream.IntStream;

public class EncodedSubLog extends EncodedLog implements OnlyCoversIndexSubset {
    private final IndexSubset indexSubset;

    protected EncodedSubLog(IndexSubset indexSubset, int[] data, int[] startIndices, IntEncoding<Activity> encoding) {
        super(new IntVectorSubsetStorage(indexSubset, data, startIndices), encoding);
        this.indexSubset = indexSubset;
    }

    public IndexSubset getIndexSubset() {
        return indexSubset;
    }

    @Override
    public BitMask variantIndices() {
        return indexSubset.getIndices();
    }

    @Override
    public IntStream streamIndices() {
        return indexSubset.streamIndices();
    }
}
