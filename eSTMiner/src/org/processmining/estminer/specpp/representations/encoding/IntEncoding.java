package org.processmining.estminer.specpp.representations.encoding;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface IntEncoding<T> extends Encoding<T, Integer>, HasDenseRange {

    int OUTSIDE_RANGE = -1;

    IntStream primitiveRange();

    @Override
    default Stream<Integer> range() {
        return primitiveRange().boxed();
    }

    boolean isIntInRange(int toDecode);

    @Override
    default boolean isInRange(Integer toDecode) {
        return isIntInRange(toDecode);
    }

}
