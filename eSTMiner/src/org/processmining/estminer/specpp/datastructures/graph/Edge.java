package org.processmining.estminer.specpp.datastructures.graph;

import org.processmining.estminer.specpp.datastructures.util.Pair;

public interface Edge<V extends Vertex> extends GraphObject {

    Pair<V> getVertices();

}
