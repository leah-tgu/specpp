package org.processmining.estminer.specpp.datastructures.encoding;

import com.google.common.collect.Streams;
import org.processmining.estminer.specpp.datastructures.util.Tuple2;
import org.processmining.estminer.specpp.traits.Immutable;

import java.util.stream.Stream;

/**
 * An immutable encoding of items of type {@code K} to elements of type {@code V} in the range.
 *
 * @param <K> the encoding domain type
 * @param <V> the encoding range type
 */
public interface Encoding<K, V> extends Immutable {

    V encode(K item);

    K decode(V value);

    default Stream<Tuple2<K, V>> pairs() {
        return Streams.zip(domain(), domain().map(this::encode), Tuple2::new);
    }

    int size();

    Stream<K> domain();

    Stream<V> range();

    boolean isInRange(V toDecode);

    boolean isInDomain(K toEncode);
}
