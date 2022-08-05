package org.processmining.estminer.specpp.supervision.traits;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.piping.Observable;

public interface ToOne<O extends Observation> extends Observable<O>, DimensionalityTrait {
}
