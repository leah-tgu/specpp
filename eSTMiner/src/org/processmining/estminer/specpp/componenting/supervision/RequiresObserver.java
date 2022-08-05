package org.processmining.estminer.specpp.componenting.supervision;

import org.processmining.estminer.specpp.supervision.observations.Observation;

public interface RequiresObserver<O extends Observation> {

    Class<O> getObservedClass();

}
