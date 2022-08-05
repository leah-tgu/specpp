package org.processmining.estminer.specpp.datastructures.tree.base.impls;

import org.processmining.estminer.specpp.datastructures.tree.base.EfficientTree;
import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.DelayedRooting;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.LocallyExpandable;

public abstract class AbstractEfficientTree<N extends TreeNode & LocallyExpandable<N>> implements EfficientTree<N>, DelayedRooting<N> {

    protected N root;

    @Override
    public N getRoot() {
        return root;
    }

    @Override
    public void setRootOnce(N root) {
        if (this.root != null) throw new DelayedRooting.Treexecption();
        this.root = root;
    }


}
