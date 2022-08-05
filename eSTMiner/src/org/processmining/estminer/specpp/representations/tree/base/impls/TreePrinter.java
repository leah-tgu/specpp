package org.processmining.estminer.specpp.representations.tree.base.impls;

import org.processmining.estminer.specpp.representations.tree.base.AnnotatableBiDiNode;
import org.processmining.estminer.specpp.representations.tree.base.BiDiTree;
import org.processmining.estminer.specpp.supervision.observations.StringObservation;
import org.processmining.estminer.specpp.supervision.piping.AbstractAsyncAwareObservable;
import org.processmining.estminer.specpp.supervision.piping.AsyncAdHocObservable;

public class TreePrinter extends AbstractAsyncAwareObservable<StringObservation> implements AsyncAdHocObservable<StringObservation> {

    private final BiDiTree<? extends AnnotatableBiDiNode<?, ?>> tree;
    private final int fromLevel;
    private final int toLevel;
    private final long nodeLimit;

    public TreePrinter(BiDiTree<? extends AnnotatableBiDiNode<?, ?>> tree, int fromLevel, int toLevel, long nodeLimit) {
        this.tree = tree;
        this.fromLevel = fromLevel;
        this.toLevel = toLevel;
        this.nodeLimit = nodeLimit;
    }

    @Override
    public StringObservation computeObservation() {
        return new StringObservation(tree.limitedToString(fromLevel, toLevel, nodeLimit));
    }

}
