package org.processmining.estminer.specpp.representations.tree.base.impls;

import org.processmining.estminer.specpp.representations.graph.Annotatable;
import org.processmining.estminer.specpp.representations.tree.base.AnnotatableBiDiNode;

import java.util.List;

public class AnnotatableBiDiNodeImpl<A> extends AbstractBiDiNode<AnnotatableBiDiNodeImpl<A>> implements Annotatable<A>, AnnotatableBiDiNode<A, AnnotatableBiDiNodeImpl<A>> {

    private A annotation;

    protected AnnotatableBiDiNodeImpl(AnnotatableBiDiNodeImpl<A> parent, List<AnnotatableBiDiNodeImpl<A>> children, A annotation) {
        super(parent, children);
        this.annotation = annotation;
    }

    public AnnotatableBiDiNodeImpl(AnnotatableBiDiNodeImpl<A> parent, A annotation) {
        super(parent);
        this.annotation = annotation;
    }

    public AnnotatableBiDiNodeImpl(AnnotatableBiDiNodeImpl<A> parent) {
        super(parent);
    }

    public AnnotatableBiDiNodeImpl() {
    }

    @Override
    public A getAnnotation() {
        return annotation;
    }

    @Override
    public void setAnnotation(A annotation) {
        this.annotation = annotation;
    }

    @Override
    public String toString() {
        return annotation != null ? annotation.toString() : "{}";
    }
}
