package org.processmining.estminer.specpp.componenting.system;

import org.processmining.estminer.specpp.traits.PartiallyOrdered;
import org.processmining.estminer.specpp.traits.ProperlyHashable;
import org.processmining.estminer.specpp.traits.ProperlyPrintable;

public interface Requirement<D, T> extends PartiallyOrdered<T>, ProperlyPrintable, ProperlyHashable {

    ComponentType componentType();

    Class<? extends D> contentClass();

}
