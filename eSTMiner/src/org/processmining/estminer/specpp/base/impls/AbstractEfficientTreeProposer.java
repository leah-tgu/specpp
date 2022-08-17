package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.Proposer;
import org.processmining.estminer.specpp.datastructures.tree.base.EfficientTree;
import org.processmining.estminer.specpp.datastructures.tree.base.TreeNode;
import org.processmining.estminer.specpp.datastructures.tree.base.traits.LocallyExpandable;
import org.processmining.estminer.specpp.datastructures.tree.iterators.PreAdvancingIterator;
import org.processmining.estminer.specpp.traits.Initializable;

public abstract class AbstractEfficientTreeProposer<C extends Candidate, N extends TreeNode & LocallyExpandable<N>> extends PreAdvancingIterator<N> implements Proposer<C>, Initializable {

    protected final EfficientTree<N> tree;
    private N previousProposedNode;

    public N getPreviousProposedNode() {
        return previousProposedNode;
    }

    protected AbstractEfficientTreeProposer(EfficientTree<N> tree) {
        this.tree = tree;
    }

    protected abstract C extractCandidate(N node);

    protected abstract boolean describesValidCandidate(N node);

    @Override
    public C proposeCandidate() {
        previousProposedNode = next();
        return extractCandidate(previousProposedNode);
    }

    @Override
    public boolean isExhausted() {
        return !hasNext();
    }

    @Override
    protected N advance() {
        N nextNode;
        do {
            nextNode = tree.expandTree();
        } while (nextNode != null && !describesValidCandidate(nextNode));
        return nextNode;
    }

    @Override
    public void init() {
        current = advance();
    }

}
