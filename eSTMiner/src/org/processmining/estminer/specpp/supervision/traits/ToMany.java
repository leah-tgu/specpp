package org.processmining.estminer.specpp.supervision.traits;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.piping.Observable;
import org.processmining.estminer.specpp.supervision.piping.Observations;

public interface ToMany<O extends Observation> extends Observable<Observations<O>>, DimensionalityTrait {
}
