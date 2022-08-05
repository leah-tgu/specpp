package org.processmining.estminer.specpp.datastructures.graph;

import org.processmining.estminer.specpp.datastructures.util.Pair;

public interface DirectedEdge<V extends Vertex> extends Edge<V> {

    V predecessor();

    V successor();

    @Override
    default Pair<V> getVertices() {
        return new Pair<>(predecessor(), successor());
    }
}
