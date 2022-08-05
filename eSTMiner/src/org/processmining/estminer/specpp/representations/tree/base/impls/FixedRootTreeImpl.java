package org.processmining.estminer.specpp.representations.tree.base.impls;

import org.processmining.estminer.specpp.representations.tree.base.Tree;
import org.processmining.estminer.specpp.representations.tree.base.TreeNode;

public class FixedRootTreeImpl<N extends TreeNode> implements Tree<N> {

    protected final N root;

    public FixedRootTreeImpl(N root) {
        this.root = root;
    }

    @Override
    public N getRoot() {
        return root;
    }

}
