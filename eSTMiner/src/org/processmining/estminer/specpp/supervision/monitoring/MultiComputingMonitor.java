package org.processmining.estminer.specpp.supervision.monitoring;

import org.processmining.estminer.specpp.datastructures.util.TypedItem;
import org.processmining.estminer.specpp.supervision.observations.Observation;

import java.util.Collection;

public interface MultiComputingMonitor<O extends Observation, R> extends Monitor<O, R> {

    Collection<TypedItem<?>> computeResults();

}
