package org.processmining.estminer.specpp.base;

import java.util.function.Supplier;

@FunctionalInterface
public interface Proposer<C extends Candidate> extends Supplier<C> {

    C proposeCandidate();

    @Override
    default C get() {
        return proposeCandidate();
    }
}
