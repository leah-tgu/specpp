package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.Proposer;
import org.processmining.estminer.specpp.representations.tree.base.TreeNode;
import org.processmining.estminer.specpp.representations.tree.base.impls.EnumeratingTree;
import org.processmining.estminer.specpp.representations.tree.base.traits.LocallyExpandable;
import org.processmining.estminer.specpp.representations.tree.iterators.PreAdvancingIterator;
import org.processmining.estminer.specpp.traits.Initializable;

public abstract class AbstractEnumeratingTreeProposer<C extends Candidate, N extends TreeNode & LocallyExpandable<N>> extends PreAdvancingIterator<N> implements Proposer<C>, Initializable {

    protected final EnumeratingTree<N> tree;
    private N previousProposedNode;

    protected N getPreviousProposedNode() {
        return previousProposedNode;
    }

    protected AbstractEnumeratingTreeProposer(EnumeratingTree<N> tree) {
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
