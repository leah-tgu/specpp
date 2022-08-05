package org.processmining.estminer.specpp.representations.tree.base.impls;

import org.processmining.estminer.specpp.representations.tree.base.Tree;
import org.processmining.estminer.specpp.representations.tree.base.TreeNode;
import org.processmining.estminer.specpp.representations.tree.base.traits.Rerootable;

public class RerootableTreeImpl<N extends TreeNode> implements Tree<N>, Rerootable<N> {

    private N root;

    public RerootableTreeImpl(N root) {
        this.root = root;
    }

    public RerootableTreeImpl() {
    }

    @Override
    public N getRoot() {
        return root;
    }

    @Override
    public void setRoot(N newRoot) {
        root = newRoot;
    }
}
