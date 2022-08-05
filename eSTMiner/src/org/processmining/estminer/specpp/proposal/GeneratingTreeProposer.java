package org.processmining.estminer.specpp.proposal;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.impls.AbstractEnumeratingTreeProposer;
import org.processmining.estminer.specpp.representations.tree.base.LocalNodeGenerator;
import org.processmining.estminer.specpp.representations.tree.base.NodeProperties;
import org.processmining.estminer.specpp.representations.tree.base.impls.EnumeratingTree;
import org.processmining.estminer.specpp.representations.tree.base.impls.GeneratingLocalNode;

public abstract class GeneratingTreeProposer<C extends Candidate & NodeProperties, N extends GeneratingLocalNode<C, ?, N>, G extends LocalNodeGenerator<C, ?, N>> extends AbstractEnumeratingTreeProposer<C, N> {

    protected final G generator;

    public GeneratingTreeProposer(G generator, EnumeratingTree<N> tree) {
        super(tree);
        this.generator = generator;
    }

    protected G getGenerator() {
        return generator;
    }

    @Override
    protected C extractCandidate(N node) {
        return node.getProperties();
    }

    @Override
    public void init() {
        tree.setRootOnce(generator.generateRoot());
        super.init();
    }


}
