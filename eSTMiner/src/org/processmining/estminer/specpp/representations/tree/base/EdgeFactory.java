package org.processmining.estminer.specpp.representations.tree.base;

import org.processmining.estminer.specpp.representations.graph.Edge;
import org.processmining.estminer.specpp.representations.graph.Vertex;
import org.processmining.estminer.specpp.representations.tree.base.impls.TreeEdgeImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface EdgeFactory<V extends Vertex, E extends Edge<V>> {

    E create(V parent, V child);

    class ReflectiveFactory<V extends Vertex, E extends Edge<V>> implements EdgeFactory<V, E> {
        private final Class<E> edgeClass;

        public ReflectiveFactory(Class<E> edgeClass) {
            this.edgeClass = edgeClass;
        }

        public E create(V parent, V child) {
            E instance = null;
            try {
                Constructor<E> constructor = edgeClass.getConstructor(parent.getClass(), child.getClass());
                instance = constructor.newInstance(parent, child);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException ignored) {
                throw new RuntimeException("Graph Edge Class does not have fitting constructor");
            }
            return instance;
        }
    }

    class BasicTreeEdgeFactory<N extends BiDiTreeNode<N>> implements EdgeFactory<N, TreeEdge<N>> {
        @Override
        public TreeEdge<N> create(N parent, N child) {
            return new TreeEdgeImpl<>(parent, child);
        }
    }

}
