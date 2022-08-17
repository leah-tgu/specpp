package org.processmining.estminer.specpp.datastructures.log.impls;

import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.IndexSubset;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncoding;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncodings;
import org.processmining.estminer.specpp.datastructures.log.Activity;
import org.processmining.estminer.specpp.datastructures.log.Log;
import org.processmining.estminer.specpp.datastructures.log.OnlyCoversIndexSubset;
import org.processmining.estminer.specpp.datastructures.log.Variant;
import org.processmining.estminer.specpp.datastructures.petri.Transition;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorStorage;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorSubsetStorage;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class LogEncoder {

    public static class LogEncodingParameters {

        private final boolean discardEmptyVariants, filterByEncodedActivities;

        public LogEncodingParameters(boolean discardEmptyVariants, boolean filterByEncodedActivities) {
            this.discardEmptyVariants = discardEmptyVariants;
            this.filterByEncodedActivities = filterByEncodedActivities;
        }

        public boolean discardEmptyVariants() {
            return discardEmptyVariants;
        }

        public boolean filterByEncodedActivities() {
            return filterByEncodedActivities;
        }

    }


    public static class LogEncodingInfo {

        private final Set<Activity> encodedActivities;

        public LogEncodingInfo(Set<Activity> encodedActivities) {
            this.encodedActivities = encodedActivities;
        }

        public Set<Activity> getEncodedActivities() {
            return encodedActivities;
        }
    }


    public static EncodedLog encodeLog(Log log, IntEncoding<Activity> encoding, LogEncodingParameters lep, LogEncodingInfo lei) {
        BitMask mask = log.variantIndices().copy();
        Set<Activity> activitySet = lei.getEncodedActivities();
        ArrayList<Integer> cumLengthsList = new ArrayList<>();
        ArrayList<Integer> dataList = new ArrayList<>();
        boolean discardedVariant = false;
        int acc = 0;
        cumLengthsList.add(acc);
        for (IndexedVariant indexedVariant : log) {
            Variant variant = indexedVariant.getItem();
            int length = 0;
            for (Activity activity : variant) {
                if (!lep.filterByEncodedActivities() || activitySet.contains(activity)) {
                    if (encoding.isInDomain(activity)) dataList.add(encoding.encode(activity));
                    else dataList.add(IntEncoding.OUTSIDE_RANGE);
                    ++length;
                }
            }
            if (length == 0 && lep.discardEmptyVariants()) {
                mask.clear(indexedVariant.getIndex());
                discardedVariant = true;
            } else {
                acc += length;
                cumLengthsList.add(acc);
            }
        }
        int[] data = dataList.stream().mapToInt(i -> i).toArray();
        int[] startIndices = cumLengthsList.stream().mapToInt(i -> i).toArray();
        if (log instanceof OnlyCoversIndexSubset || discardedVariant) {
            IntVectorSubsetStorage ivss = new IntVectorSubsetStorage(IndexSubset.of(mask), data, startIndices);
            return new EncodedSubLogImpl(log.getVariantFrequencies(), ivss, encoding);
        } else {
            IntVectorStorage ivs = new IntVectorStorage(data, startIndices);
            return new EncodedLogImpl(log.getVariantFrequencies(), ivs, encoding);
        }
    }

    public static MultiEncodedLog multiEncodeLog(Log log, IntEncodings<Transition> transitionEncodings, Map<Activity, Transition> mapping, LogEncodingParameters lep) {
        IntEncodings<Activity> activityEncodings = IntEncodings.mapEncodings(transitionEncodings, mapping);
        return multiEncodeLog(log, activityEncodings, lep);
    }

    public static MultiEncodedLog multiEncodeLog(Log log, IntEncodings<Activity> activityEncodings, LogEncodingParameters lep) {
        Set<Activity> activitySet = activityEncodings.combinedDomain();
        LogEncodingInfo lei = new LogEncodingInfo(activitySet);
        EncodedLog presetEncodedLog = encodeLog(log, activityEncodings.pre(), lep, lei);
        EncodedLog postsetEncodedLog = encodeLog(log, activityEncodings.post(), lep, lei);
        if (presetEncodedLog instanceof EncodedSubLogImpl && postsetEncodedLog instanceof EncodedSubLogImpl) {
            IndexSubset indexSubset = ((OnlyCoversIndexSubset) presetEncodedLog).getIndexSubset();
            return new MultiEncodedSubLog(indexSubset, ((EncodedSubLogImpl) presetEncodedLog), ((EncodedSubLogImpl) postsetEncodedLog), activityEncodings);
        } else return new MultiEncodedLog(presetEncodedLog, postsetEncodedLog, activityEncodings);
    }

}
