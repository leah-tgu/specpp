package org.processmining.estminer.specpp.componenting.supervision;

import org.processmining.estminer.specpp.componenting.system.ComponentType;
import org.processmining.estminer.specpp.componenting.system.FulfilledRequirement;
import org.processmining.estminer.specpp.componenting.system.FulfilledRequirementsCollection;
import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.piping.AdHocObservable;
import org.processmining.estminer.specpp.supervision.piping.Observable;
import org.processmining.estminer.specpp.supervision.piping.ObservationPipe;
import org.processmining.estminer.specpp.supervision.piping.Observer;

public class SupervisorCollection extends FulfilledRequirementsCollection<SupervisionRequirement> {

    public void register(AbstractFulfilledSupervisionRequirement<?> fulfilledSupervisionRequirement) {
        add(fulfilledSupervisionRequirement);
    }

    @Override
    public ComponentType componentType() {
        return ComponentType.Supervision;
    }

    public <O extends Observation> Observable<O> askForObservable(ObservableRequirement<O> requirement) {
        FulfilledRequirement<Observable<O>, SupervisionRequirement> fulfilledRequirement = satisfyRequirement(requirement);
        return fulfilledRequirement.getContent();
    }

    public <O extends Observation> AdHocObservable<O> askForAdHocObservable(AdHocObservableRequirement<O> requirement) {
        FulfilledRequirement<AdHocObservable<O>, SupervisionRequirement> fulfilledRequirement = satisfyRequirement(requirement);
        return fulfilledRequirement.getContent();
    }

    public <O extends Observation> Observer<O> askForObserver(ObserverRequirement<O> requirement) {
        FulfilledRequirement<Observer<O>, SupervisionRequirement> fulfilledRequirement = satisfyRequirement(requirement);
        return fulfilledRequirement.getContent();
    }

    public <I extends Observation, O extends Observation> Observable<O> askForPipe(ObservationPipeRequirement<I, O> requirement) {
        FulfilledRequirement<ObservationPipe<I, O>, SupervisionRequirement> fulfilledRequirement = satisfyRequirement(requirement);
        return fulfilledRequirement.getContent();
    }

}
