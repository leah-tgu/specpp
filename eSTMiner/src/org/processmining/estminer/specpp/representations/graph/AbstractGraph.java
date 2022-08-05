package org.processmining.estminer.specpp.representations.graph;

import org.apache.commons.collections4.IteratorUtils;
import org.processmining.estminer.specpp.util.datastructures.Pair;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractGraph<V extends Vertex, E extends Edge<V>> implements Graph<V, E>, GraphMutation<V, E> {

    private final Set<V> vertices;
    private final Set<E> edges;


    protected AbstractGraph() {
        vertices = new HashSet<>();
        edges = new HashSet<>();
    }

    @Override
    public void addVertex(V vertex) {
        vertices.add(vertex);
    }

    @Override
    public void addEdge(E edge) {
        Pair<V> pair = edge.getVertices();
        vertices.add(pair.first());
        vertices.add(pair.second());
        edges.add(edge);
    }

    @Override
    public Iterable<V> getVertices() {
        return IteratorUtils.asIterable(vertices.iterator());
    }

    @Override
    public Iterable<E> getEdges() {
        return IteratorUtils.asIterable(edges.iterator());
    }

}
