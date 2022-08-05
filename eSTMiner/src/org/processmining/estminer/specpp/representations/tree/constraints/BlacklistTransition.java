package org.processmining.estminer.specpp.representations.tree.constraints;

import org.processmining.estminer.specpp.representations.petri.Transition;
import org.processmining.estminer.specpp.representations.tree.base.GenerationConstraint;

public class BlacklistTransition implements GenerationConstraint {
    private final Transition transition;

    public BlacklistTransition(Transition transition) {
        this.transition = transition;
    }

    public Transition getTransition() {
        return transition;
    }

    @Override
    public String toString() {
        return "BlacklistTransitions(" + transition + ")";
    }

}
