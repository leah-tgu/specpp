package org.processmining.estminer.specpp.representations.tree.iterators;

import com.google.common.collect.Lists;
import org.processmining.estminer.specpp.representations.tree.base.Tree;
import org.processmining.estminer.specpp.representations.tree.base.UniDiTreeNode;

import java.util.Deque;

@Deprecated
public class BottomUpTraversal<N extends UniDiTreeNode<N>> extends TreeNodeTraversal<N> {

    private Deque<N> stack;

    public BottomUpTraversal(Tree<N> tree) {
        stack = Lists.newLinkedList();
        addSubtree(tree.getRoot());
        advance();
    }

    private void addSubtree(N node) {
        stack.addFirst(node);
        if (!node.isLeaf()) {
            for (N child : node.getChildren()) {
                addSubtree(child);
            }
        }
    }


    @Override
    protected N getNextNode() {
        return stack.pollFirst();
    }
}
