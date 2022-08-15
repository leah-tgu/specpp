package org.processmining.estminer.specpp.config.parameters;

import org.processmining.estminer.specpp.traits.PrettyPrintable;
import org.processmining.estminer.specpp.traits.ProperlyPrintable;

public interface Parameters extends ProperlyPrintable, PrettyPrintable {

    @Override
    default String toPrettyString() {
        return toString();
    }
}
