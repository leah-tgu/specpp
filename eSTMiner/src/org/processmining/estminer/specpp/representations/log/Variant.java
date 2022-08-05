package org.processmining.estminer.specpp.representations.log;

import org.processmining.estminer.specpp.traits.Immutable;
import org.processmining.estminer.specpp.traits.ProperlyHashable;
import org.processmining.estminer.specpp.traits.ProperlyPrintable;
import org.processmining.estminer.specpp.traits.Streamable;

public interface Variant extends Iterable<Activity>, Streamable<Activity>, ProperlyPrintable, ProperlyHashable, Immutable {

    int getLength();

    default int size() {
        return getLength();
    }

}
