package org.processmining.estminer.specpp.componenting.supervision;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.piping.AdHocObservable;

public class FulfilledAdHocObservableRequirement<O extends Observation> extends FulfilledObservableRequirement<O> {

    public FulfilledAdHocObservableRequirement(AdHocObservableRequirement<?> adHocObservableRequirement, AdHocObservable<O> observable) {
        super(adHocObservableRequirement, observable);
    }
}
