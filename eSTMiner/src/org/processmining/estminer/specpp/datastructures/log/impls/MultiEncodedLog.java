package org.processmining.estminer.specpp.datastructures.log.impls;

import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncodings;
import org.processmining.estminer.specpp.datastructures.log.Activity;
import org.processmining.estminer.specpp.datastructures.util.ImmutablePair;
import org.processmining.estminer.specpp.datastructures.util.IndexedItem;
import org.processmining.estminer.specpp.datastructures.util.Tuple2;
import org.processmining.estminer.specpp.datastructures.vectorization.spliterators.EfficientlySpliterable;
import org.processmining.estminer.specpp.util.CompatiblePairSpliteratorImpl;
import org.processmining.estminer.specpp.util.StreamUtils;

import java.util.Spliterator;
import java.util.stream.IntStream;

public class MultiEncodedLog implements EfficientlySpliterable<Tuple2<IntStream, IntStream>> {

    private final EncodedLog presetEncodedLog;
    private final EncodedLog postsetEncodedLog;
    private final IntEncodings<Activity> activityEncodings;

    protected MultiEncodedLog(EncodedLog presetEncodedLog, EncodedLog postsetEncodedLog, IntEncodings<Activity> activityEncodings) {
        assert presetEncodedLog.variantCount() == postsetEncodedLog.variantCount();
        assert StreamUtils.streamsEqual(presetEncodedLog.streamIndices(), postsetEncodedLog.streamIndices());
        this.presetEncodedLog = presetEncodedLog;
        this.postsetEncodedLog = postsetEncodedLog;
        this.activityEncodings = activityEncodings;
    }

    public EncodedLog getPresetEncodedLog() {
        return presetEncodedLog;
    }

    public EncodedLog pre() {
        return getPresetEncodedLog();
    }

    public EncodedLog getPostsetEncodedLog() {
        return postsetEncodedLog;
    }

    public EncodedLog post() {
        return getPostsetEncodedLog();
    }

    public IntEncodings<Activity> getEncodings() {
        return activityEncodings;
    }

    public int getVariantCount() {
        return getPresetEncodedLog().variantCount();
    }

    public BitMask variantIndices() {
        return getPresetEncodedLog().variantIndices();
    }

    public IntStream streamIndices() {
        return getPresetEncodedLog().streamIndices();
    }

    @Override
    public String toString() {
        return "MultiEncodedLog{" + "presetEncodedLog=" + presetEncodedLog + ", postsetEncodedLog=" + postsetEncodedLog + "}";
    }

    @Override
    public Spliterator<Tuple2<IntStream, IntStream>> efficientSpliterator() {
        Spliterator<IntStream> spliteratorA = presetEncodedLog.efficientSpliterator();
        Spliterator<IntStream> spliteratorB = postsetEncodedLog.efficientSpliterator();
        CompatiblePairSpliteratorImpl<IntStream, Tuple2<IntStream, IntStream>> res = new CompatiblePairSpliteratorImpl<>(spliteratorA, spliteratorB, tup -> tup);
        return res;
    }

    @Override
    public Spliterator<IndexedItem<Tuple2<IntStream, IntStream>>> efficientIndexedSpliterator() {
        Spliterator<IndexedItem<IntStream>> spliteratorA = presetEncodedLog.efficientIndexedSpliterator();
        Spliterator<IndexedItem<IntStream>> spliteratorB = postsetEncodedLog.efficientIndexedSpliterator();
        return new CompatiblePairSpliteratorImpl<>(spliteratorA, spliteratorB, tup -> {
            IndexedItem<IntStream> t1 = tup.getT1();
            IndexedItem<IntStream> t2 = tup.getT2();
            return new IndexedItem<>(t1.getIndex(), new ImmutablePair<>(t1.getItem(), t2.getItem()));
        });
    }
}
