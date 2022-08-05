package org.processmining.estminer.specpp.datastructures.tree.base.impls;

import org.processmining.estminer.specpp.datastructures.graph.Annotatable;
import org.processmining.estminer.specpp.datastructures.tree.base.AnnotatableBiDiNode;
import org.processmining.estminer.specpp.datastructures.tree.base.BiDiTreeNode;
import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.MutableChildren;
import org.processmining.estminer.specpp.util.Reflection;

@SuppressWarnings("unchecked")
public class ReflectiveNodeFactory {


    public static <N extends TreeNode> N root(Class<N> nodeClass) {
        return Reflection.instance(nodeClass);
    }

    public static <A, N extends TreeNode & Annotatable<A>> N annotated(N node, A annotation) {
        node.setAnnotation(annotation);
        return node;
    }

    public static <A, N extends TreeNode & Annotatable<A>> N annotatedRoot(Class<N> nodeClass, A annotation) {
        return annotated(root(nodeClass), annotation);
    }

    public static <N extends BiDiTreeNode<N>> N childOf(N parent) {
        N instance = (N) (Reflection.instance(parent.getClass(), parent));
        if (parent instanceof MutableChildren) ((MutableChildren<N>) parent).addChild(instance);
        return instance;
    }

    public static <A, N extends AnnotatableBiDiNode<A, N>> N annotatedChildOf(N parent, A annotation) {
        return annotated(childOf(parent), annotation);
    }

}
