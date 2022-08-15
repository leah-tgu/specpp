package org.processmining.estminer.specpp.supervision.monitoring;

import com.google.common.collect.ImmutableList;
import org.processmining.estminer.specpp.datastructures.util.TypedItem;
import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.traits.ProvidesResults;

import java.util.Collection;

public interface ComputingMonitor<O extends Observation, R, F> extends Monitor<O, R>, ProvidesResults {

    F computeResult();

    @Override
    default Collection<TypedItem<?>> getResults() {
        F r = computeResult();
        return ImmutableList.of(new TypedItem<>(r.getClass(), r));
    }
}
