package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.Observation;

import java.util.function.Function;

@FunctionalInterface
public interface ObservationTransformer<I extends Observation, O extends Observation> extends Function<I, O> {

}
