package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.datastructures.tree.base.EfficientTree;
import org.processmining.estminer.specpp.datastructures.tree.base.LocalNodeGenerator;
import org.processmining.estminer.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.GeneratingLocalNode;

public abstract class GeneratingTreeProposer<C extends Candidate & NodeProperties, N extends GeneratingLocalNode<C, ?, N>, G extends LocalNodeGenerator<C, ?, N>> extends AbstractEfficientTreeProposer<C, N> {

    protected final G generator;

    public GeneratingTreeProposer(G generator, EfficientTree<N> tree) {
        super(tree);
        this.generator = generator;
    }

    G getGenerator() {
        return generator;
    }

    @Override
    protected C extractCandidate(N node) {
        return node.getProperties();
    }

    @Override
    public void init() {
        tree.setRootOnce(generator.generateRoot());
    }


}
