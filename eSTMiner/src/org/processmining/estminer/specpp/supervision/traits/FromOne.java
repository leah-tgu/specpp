package org.processmining.estminer.specpp.supervision.traits;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.piping.Observer;

public interface FromOne<I extends Observation> extends Observer<I>, DimensionalityTrait {
}
