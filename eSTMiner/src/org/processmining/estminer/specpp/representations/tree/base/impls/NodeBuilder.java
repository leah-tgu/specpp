package org.processmining.estminer.specpp.representations.tree.base.impls;

import org.processmining.estminer.specpp.representations.tree.base.TreeNode;
import org.processmining.estminer.specpp.representations.tree.base.traits.KnowsParent;
import org.processmining.estminer.specpp.representations.tree.base.traits.MutableChildren;
import org.processmining.estminer.specpp.util.Reflection;

public class NodeBuilder<N extends TreeNode> extends NodeFactory {

    private final Class<N> nodeClass;

    public NodeBuilder(Class<N> nodeClass) {
        this.nodeClass = nodeClass;
    }

    public N createNode(Object... args) {
        return Reflection.instance(nodeClass, args);
    }


    public N childOf(N parent) {
        N instance = parent instanceof KnowsParent ? createNode(parent) : createNode();
        if (parent instanceof MutableChildren) ((MutableChildren<N>) parent).addChild(instance);
        return instance;
    }


}
