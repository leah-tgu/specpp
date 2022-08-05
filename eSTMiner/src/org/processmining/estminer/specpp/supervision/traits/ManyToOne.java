package org.processmining.estminer.specpp.supervision.traits;

import org.processmining.estminer.specpp.supervision.observations.Observation;

public interface ManyToOne<I extends Observation, O extends Observation> extends FromMany<I>, ToOne<O> {
}
