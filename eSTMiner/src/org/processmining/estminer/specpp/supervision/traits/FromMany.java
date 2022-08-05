package org.processmining.estminer.specpp.supervision.traits;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.piping.Observations;
import org.processmining.estminer.specpp.supervision.piping.Observer;

public interface FromMany<O extends Observation> extends Observer<Observations<O>>, DimensionalityTrait {
}
