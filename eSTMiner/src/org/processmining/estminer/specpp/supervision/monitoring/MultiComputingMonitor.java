package org.processmining.estminer.specpp.supervision.monitoring;

import org.processmining.estminer.specpp.datastructures.util.TypedItem;
import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.traits.ProvidesResults;

import java.util.Collection;

public interface MultiComputingMonitor<O extends Observation, R> extends Monitor<O, R>, ProvidesResults {

    Collection<TypedItem<?>> computeResults();

    @Override
    default Collection<TypedItem<?>> getResults() {
        return computeResults();
    }
}
