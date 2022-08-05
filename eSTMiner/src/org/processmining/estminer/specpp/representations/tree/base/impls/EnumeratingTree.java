package org.processmining.estminer.specpp.representations.tree.base.impls;

import org.apache.commons.collections4.IteratorUtils;
import org.processmining.estminer.specpp.representations.tree.base.ExpansionStrategy;
import org.processmining.estminer.specpp.representations.tree.base.TreeNode;
import org.processmining.estminer.specpp.representations.tree.base.traits.LocallyExpandable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class EnumeratingTree<N extends TreeNode & LocallyExpandable<N>> extends AbstractEfficientTree<N> {

    private final ExpansionStrategy<N> expansionStrategy;
    protected final Set<N> leaves;
    private N lastExpansion;

    public EnumeratingTree(ExpansionStrategy<N> expansionStrategy) {
        this.expansionStrategy = expansionStrategy;
        this.leaves = new HashSet<>();
    }

    public EnumeratingTree(N root, ExpansionStrategy<N> expansionStrategy) {
        this.expansionStrategy = expansionStrategy;
        this.leaves = new HashSet<>();
        setRootOnce(root);
    }

    @Override
    public Iterator<N> getLeaves() {
        return leaves.iterator();
    }

    protected final N expandNode(N node) {
        N child = node.generateChild();
        insertNewNode(child);
        softExpand(child);
        nodeExpanded(node, child);
        return child;
    }

    protected void insertNewNode(N node) {
        addLeaf(node);
        expansionStrategy.registerNode(node);
    }


    protected boolean addLeaf(N node) {
        return leaves.add(node);
    }

    protected boolean removeLeaf(N node) {
        return leaves.remove(node);
    }

    protected void softExpand(N child) {
        expansionStrategy.registerPotentialNodes(child.generatePotentialChildren());
    }

    protected N expand() {
        boolean canExpand = false;
        N prospectiveExpansion = null;
        while (expansionStrategy.hasNextExpansion() && !canExpand) {
            prospectiveExpansion = expansionStrategy.nextExpansion();
            canExpand = prospectiveExpansion.canExpand();
            if (!canExpand) lastProposalNotExpandable(prospectiveExpansion);
        }
        if (canExpand) return expandNode(prospectiveExpansion);
        else return null;
    }

    protected void lastProposalNotExpandable(N node) {
        expansionStrategy.deregisterPreviousProposal();
        notExpandable(node);
    }

    protected void notExpandable(N node) {
        removeLeaf(node);
    }

    protected void nodeExpanded(N node, N child) {
        lastExpansion = node;
        if (!node.canExpand()) lastProposalNotExpandable(node);
        else removeLeaf(node);
    }

    @Override
    public N expandTree() {
        return expand();
    }

    @Override
    public void setRootOnce(N root) {
        super.setRootOnce(root);
        insertNewNode(root);
    }

    @Override
    public String toString() {
        return Objects.toString(lastExpansion) + "::" + IteratorUtils.toString(getLeaves());
    }

}
