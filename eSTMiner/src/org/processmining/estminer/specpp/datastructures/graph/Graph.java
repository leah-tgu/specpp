package org.processmining.estminer.specpp.datastructures.graph;

public interface Graph<V extends Vertex, E extends Edge<V>> {

    Iterable<V> getVertices();

    Iterable<E> getEdges();

}
