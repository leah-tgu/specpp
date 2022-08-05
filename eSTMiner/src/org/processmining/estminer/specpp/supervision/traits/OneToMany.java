package org.processmining.estminer.specpp.supervision.traits;

import org.processmining.estminer.specpp.supervision.observations.Observation;

public interface OneToMany<I extends Observation, O extends Observation> extends FromOne<I>, ToMany<O> {
}
