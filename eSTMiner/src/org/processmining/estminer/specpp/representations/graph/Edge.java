package org.processmining.estminer.specpp.representations.graph;

import org.processmining.estminer.specpp.util.datastructures.Pair;

public interface Edge<V extends Vertex> extends GraphObject {

    Pair<V> getVertices();

}
