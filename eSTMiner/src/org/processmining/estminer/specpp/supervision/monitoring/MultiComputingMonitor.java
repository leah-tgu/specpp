package org.processmining.estminer.specpp.supervision.monitoring;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.util.datastructures.TypedItem;

import java.util.Collection;

public interface MultiComputingMonitor<O extends Observation, R> extends Monitor<O, R> {

    Collection<TypedItem<?>> computeResults();

}
