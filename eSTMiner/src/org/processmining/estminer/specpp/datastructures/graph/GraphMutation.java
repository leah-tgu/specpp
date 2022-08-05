package org.processmining.estminer.specpp.datastructures.graph;

public interface GraphMutation<V extends Vertex, E extends Edge<V>> {

    void addVertex(V vertex);

    void addEdge(E edge);

}
