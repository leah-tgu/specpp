package org.processmining.estminer.specpp.representations.log.impls;

import org.processmining.estminer.specpp.representations.BitMask;
import org.processmining.estminer.specpp.representations.encoding.IndexSubset;
import org.processmining.estminer.specpp.representations.encoding.IntEncoding;
import org.processmining.estminer.specpp.representations.log.Activity;
import org.processmining.estminer.specpp.representations.log.OnlyCoversIndexSubset;
import org.processmining.estminer.specpp.representations.vectorization.IntVectorSubsetStorage;

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
