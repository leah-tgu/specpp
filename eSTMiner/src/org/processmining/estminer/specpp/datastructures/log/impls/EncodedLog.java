package org.processmining.estminer.specpp.datastructures.log.impls;

import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncoding;
import org.processmining.estminer.specpp.datastructures.log.Activity;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVector;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVectorStorage;
import org.processmining.estminer.specpp.datastructures.vectorization.spliteration.IndexedSpliterable;
import org.processmining.estminer.specpp.datastructures.vectorization.spliteration.Spliterable;

import java.nio.IntBuffer;
import java.util.stream.IntStream;

public interface EncodedLog extends Spliterable<IntBuffer>, IndexedSpliterable<IntBuffer> {
    IntEncoding<Activity> getEncoding();

    IntVectorStorage getEncodedVariantVectors();

    IntVector getVariantFrequencies();

    int getVariantFrequency(int index);

    IntBuffer getEncodedVariant(int index);

    IntStream streamIndices();

    BitMask variantIndices();

    int variantCount();

    int totalTraceCount();

}
