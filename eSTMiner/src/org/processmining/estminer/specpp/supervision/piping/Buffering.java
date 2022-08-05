package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.traits.Triggerable;

@FunctionalInterface
public interface Buffering extends Triggerable {

    void flushBuffer();

    @Override
    default void trigger() {
        flushBuffer();
    }

}
