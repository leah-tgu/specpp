package org.processmining.estminer.specpp.traits;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.piping.Observable;

public interface ExposesObservable<O extends Observation> {

    Observable<O> getObservable();

}
