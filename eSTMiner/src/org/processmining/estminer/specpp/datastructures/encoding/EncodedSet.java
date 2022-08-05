package org.processmining.estminer.specpp.datastructures.encoding;

public interface EncodedSet<K, V> extends SlightlyMutableSet<K>, Iterable<K> {

    class InconsistentEncodingException extends RuntimeException {

    }

    Encoding<K, V> getEncoding();


}
