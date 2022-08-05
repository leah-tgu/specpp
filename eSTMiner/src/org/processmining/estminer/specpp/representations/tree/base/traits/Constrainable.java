package org.processmining.estminer.specpp.representations.tree.base.traits;

import org.processmining.estminer.specpp.base.ConstraintEvent;
import org.processmining.estminer.specpp.supervision.piping.Observer;

public interface Constrainable<C extends ConstraintEvent> extends Observer<C> {

    void acceptConstraint(C constraint);

    @Override
    default void observe(C constraint) {
        acceptConstraint(constraint);
    }

}
