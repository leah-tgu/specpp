package org.processmining.estminer.specpp.supervision.traits;

import org.processmining.estminer.specpp.supervision.observations.Observation;

public interface ManyToMany<I extends Observation, O extends Observation> extends FromMany<I>, ToMany<O> {
}
