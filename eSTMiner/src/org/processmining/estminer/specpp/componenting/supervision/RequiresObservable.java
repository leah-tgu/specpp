package org.processmining.estminer.specpp.componenting.supervision;

import org.processmining.estminer.specpp.supervision.observations.Observation;

public interface RequiresObservable<O extends Observation> {

    Class<O> getObservableClass();

}
