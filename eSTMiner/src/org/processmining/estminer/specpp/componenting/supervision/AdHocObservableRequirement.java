package org.processmining.estminer.specpp.componenting.supervision;

import org.processmining.estminer.specpp.datastructures.util.Label;
import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.piping.AdHocObservable;
import org.processmining.estminer.specpp.supervision.piping.Observable;
import org.processmining.estminer.specpp.util.JavaTypingUtils;
import org.processmining.estminer.specpp.util.PrintingUtils;

public class AdHocObservableRequirement<O extends Observation> extends ObservableRequirement<O> {

    public AdHocObservableRequirement(String label, Class<O> observableClass) {
        this(new Label(label), observableClass);
    }


    public AdHocObservableRequirement(Label label, Class<O> observableClass) {
        super(label, observableClass);
    }

    @Override
    public boolean gt(SupervisionRequirement other) {
        return other instanceof AdHocObservableRequirement && super.gt(other);
    }

    @Override
    public boolean lt(SupervisionRequirement other) {
        return other instanceof AdHocObservableRequirement && super.lt(other);
    }

    @Override
    public Class<Observable<O>> contentClass() {
        return JavaTypingUtils.castClass(AdHocObservable.class);
    }

    @Override
    public String toString() {
        return "AdHocObservableRequirement(" + label + ", " + getObservableClass().getSimpleName() + ")";
    }

    public FulfilledAdHocObservableRequirement<O> fulfilWith(AdHocObservable<O> observable) {
        return new FulfilledAdHocObservableRequirement<>(this, observable);
    }

}
