package org.processmining.estminer.specpp.componenting.supervision;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.piping.Observable;

public class FulfilledObservableRequirement<O extends Observation> extends AbstractFulfilledSupervisionRequirement<Observable<O>> {

    public FulfilledObservableRequirement(ObservableRequirement<?> requirement, Observable<O> delegate) {
        super(requirement, delegate);
    }

}
