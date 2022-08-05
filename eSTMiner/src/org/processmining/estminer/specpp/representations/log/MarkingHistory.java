package org.processmining.estminer.specpp.representations.log;

import com.google.common.collect.ImmutableMap;
import org.processmining.estminer.specpp.representations.encoding.HashmapEncoding;
import org.processmining.estminer.specpp.representations.petri.Transition;
import org.processmining.estminer.specpp.representations.vectorization.IntVectorStorage;

public class MarkingHistory {

    private HashmapEncoding<Integer> indexMap;
    private HashmapEncoding<Transition> transitionEncoding;
    private IntVectorStorage vectorStorage;

    public MarkingHistory() {
        indexMap = HashmapEncoding.copyOf(ImmutableMap.of(0, 0, 1, 1, 2, 2));
        transitionEncoding = HashmapEncoding.copyOf(ImmutableMap.of());
    }
}
