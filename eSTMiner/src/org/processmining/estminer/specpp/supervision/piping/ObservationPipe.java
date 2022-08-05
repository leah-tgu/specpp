package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.Observation;

public interface ObservationPipe<I extends Observation, O extends Observation> extends Observer<I>, Observable<O> {

}
