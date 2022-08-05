package org.processmining.estminer.specpp.representations.graph;

public interface Graph<V extends Vertex, E extends Edge<V>> {

    Iterable<V> getVertices();

    Iterable<E> getEdges();

}
