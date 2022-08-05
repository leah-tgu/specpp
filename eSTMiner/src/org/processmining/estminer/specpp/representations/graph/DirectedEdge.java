package org.processmining.estminer.specpp.representations.graph;

import org.processmining.estminer.specpp.util.datastructures.Pair;

public interface DirectedEdge<V extends Vertex> extends Edge<V> {

    V predecessor();

    V successor();

    @Override
    default Pair<V> getVertices() {
        return new Pair<>(predecessor(), successor());
    }
}
