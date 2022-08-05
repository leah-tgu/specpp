package org.processmining.estminer.specpp.representations.graph;

public interface GraphMutation<V extends Vertex, E extends Edge<V>> {

    void addVertex(V vertex);

    void addEdge(E edge);

}
