package org.processmining.estminer.specpp.supervision.traits;

import org.processmining.estminer.specpp.supervision.observations.Observation;

public interface OneToOne<O extends Observation, C extends Observation> extends FromOne<O>, ToOne<C> {
}
