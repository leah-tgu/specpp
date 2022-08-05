package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.Observation;

public interface Observations<O extends Observation> extends Iterable<O>, Observation {
}
