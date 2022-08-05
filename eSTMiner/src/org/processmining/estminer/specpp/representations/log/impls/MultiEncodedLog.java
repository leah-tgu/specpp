package org.processmining.estminer.specpp.representations.log.impls;

import com.google.common.collect.Streams;
import org.processmining.estminer.specpp.representations.BitMask;
import org.processmining.estminer.specpp.representations.encoding.IntEncodings;
import org.processmining.estminer.specpp.representations.log.Activity;
import org.processmining.estminer.specpp.traits.Streamable;
import org.processmining.estminer.specpp.util.StreamUtils;
import org.processmining.estminer.specpp.util.datastructures.IndexedItem;
import org.processmining.estminer.specpp.util.datastructures.Pair;

import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MultiEncodedLog implements Streamable<IndexedItem<Pair<IntStream>>>, Iterable<IndexedItem<Pair<IntStream>>> {

    private final EncodedLog presetEncodedLog;
    private final EncodedLog postsetEncodedLog;
    private final IntEncodings<Activity> activityEncodings;

    protected MultiEncodedLog(EncodedLog presetEncodedLog, EncodedLog postsetEncodedLog, IntEncodings<Activity> activityEncodings) {
        assert presetEncodedLog.getVariantCount() == postsetEncodedLog.getVariantCount();
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
        return getPresetEncodedLog().getVariantCount();
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
    public Stream<IndexedItem<Pair<IntStream>>> stream() {
        Stream<IndexedItem<IntStream>> s1 = presetEncodedLog.stream();
        Stream<IndexedItem<IntStream>> s2 = postsetEncodedLog.stream();
        return Streams.zip(s1, s2, (ii1, ii2) -> {
            assert ii1.getIndex() == ii2.getIndex();
            return new IndexedItem<>(ii1.getIndex(), new Pair<>(ii1.getItem(), ii2.getItem()));
        });
    }

    @Override
    public Iterator<IndexedItem<Pair<IntStream>>> iterator() {
        return stream().iterator();
    }
}
