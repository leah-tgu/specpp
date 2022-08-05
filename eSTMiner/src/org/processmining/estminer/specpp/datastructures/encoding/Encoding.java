package org.processmining.estminer.specpp.datastructures.encoding;

import com.google.common.collect.Streams;
import org.processmining.estminer.specpp.datastructures.util.Tuple2;
import org.processmining.estminer.specpp.traits.Immutable;

import java.util.stream.Stream;

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
