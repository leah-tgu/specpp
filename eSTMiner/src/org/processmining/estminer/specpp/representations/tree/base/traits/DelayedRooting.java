package org.processmining.estminer.specpp.representations.tree.base.traits;

import org.processmining.estminer.specpp.representations.tree.base.TreeNode;

public interface DelayedRooting<N extends TreeNode> {

    void setRootOnce(N root);

    class Treexecption extends RuntimeException {
    }
}
