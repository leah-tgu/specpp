package org.processmining.estminer.specpp.representations.tree.base.impls;

import org.processmining.estminer.specpp.representations.tree.base.BiDiTreeNode;
import org.processmining.estminer.specpp.representations.tree.base.traits.MutableChildren;
import org.processmining.estminer.specpp.representations.tree.base.traits.MutableParent;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractBiDiNode<N extends BiDiTreeNode<N>> implements BiDiTreeNode<N>, MutableChildren<N>, MutableParent<N> {

    private final List<N> children;
    private N parent;

    protected AbstractBiDiNode(N parent, List<N> children) {
        this.parent = parent;
        this.children = children;
    }

    public AbstractBiDiNode(N parent) {
        this.parent = parent;
        this.children = new LinkedList<>();
    }

    protected AbstractBiDiNode() {
        this(null);
    }

    @Override
    public List<N> getChildren() {
        return children;
    }

    @Override
    public N getParent() {
        return parent;
    }


    @Override
    public void setParent(N parent) {
        this.parent = parent;
    }

    @Override
    public void addChild(N child) {
        children.add(child);
    }

}
