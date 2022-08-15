package org.processmining.estminer.specpp.supervision.traits;

import org.processmining.estminer.specpp.datastructures.util.TypedItem;

import java.util.Collection;

public interface ProvidesResults {

    Collection<TypedItem<?>> getResults();

}
