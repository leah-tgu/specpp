package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.traits.Triggerable;

public interface Buffering extends Triggerable {

    void flushBuffer();

    boolean isBufferNonEmpty();

    @Override
    default void trigger() {
        flushBuffer();
    }

}
