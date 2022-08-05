package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.Observation;

public interface AsyncObservationPipe<I extends Observation, O extends Observation> extends ObservationPipe<I, O>, AsyncObserver<I>, AsyncObservable<O> {


}
