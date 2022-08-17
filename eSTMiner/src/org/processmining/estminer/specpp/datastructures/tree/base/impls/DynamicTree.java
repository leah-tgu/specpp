package org.processmining.estminer.specpp.datastructures.tree.base.impls;

import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.processmining.estminer.specpp.datastructures.tree.base.EfficientBacktrackableTree;
import org.processmining.estminer.specpp.datastructures.tree.base.LocalTreeTraversalStrategy;
import org.processmining.estminer.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.estminer.specpp.datastructures.tree.base.NodeState;

@Deprecated
public class DynamicTree<P extends NodeProperties, S extends NodeState, N extends AbstractLocalNode<P, S, N>> extends EventingEnumeratingTree<N> implements EfficientBacktrackableTree<N> {

    private final LocalTreeTraversalStrategy<N> traversalStrategy;
    private final MultiValuedMap<N, N> subtreeRelation;

    public DynamicTree(N root, LocalTreeTraversalStrategy<N> traversalStrategy) {
        super(root, traversalStrategy);
        this.traversalStrategy = traversalStrategy;
        subtreeRelation = MultiMapUtils.newListValuedHashMap();
    }


    @Override
    protected void notExpandable(N node) {
        super.notExpandable(node);
        if (node.canContract()) contractNode(node);
    }

    private N contractNode(N node) {
        N parent = node.generateParent();
        removeNode(node);
        if (!parent.canExpand() && parent.canContract()) return contractNode(parent);
        else if (parent.canContract()) {
            insertNewNode(parent);
            return parent;
        } else return null;
    }

    private void removeNode(N node) {

    }

    private N contract() {
        N node = traversalStrategy.nextContraction();
        if (node != null && !node.isRoot() && node.canContract()) {
            return contractNode(node);
        } else return null;
    }


    @Override
    public N contractTree() {
        return contract();
    }
}
