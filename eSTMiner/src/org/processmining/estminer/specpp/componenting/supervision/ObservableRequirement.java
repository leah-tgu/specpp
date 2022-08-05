package org.processmining.estminer.specpp.componenting.supervision;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.supervision.piping.Observable;
import org.processmining.estminer.specpp.util.JavaTypingUtils;
import org.processmining.estminer.specpp.util.PrintingUtils;
import org.processmining.estminer.specpp.util.datastructures.Label;

public class ObservableRequirement<O extends Observation> extends SupervisionRequirement implements RequiresObservable<O> {

    private final Class<O> observableClass;

    public ObservableRequirement(String label, Class<O> observableClass) {
        this(new Label(label), observableClass);
    }

    public ObservableRequirement(Label label, Class<O> observableClass) {
        super(label);
        this.observableClass = observableClass;
    }

    public Class<O> getObservableClass() {
        return observableClass;
    }

    @Override
    public boolean gt(SupervisionRequirement other) {
        if (labelIsGt(other) && other instanceof ObservableRequirement) {
            ObservableRequirement<?> r = (ObservableRequirement<?>) other;
            return r.getObservableClass().isAssignableFrom(observableClass);
        }
        return false;
    }

    @Override
    public boolean lt(SupervisionRequirement other) {
        if (labelIsLt(other) && other instanceof RequiresObservable) {
            RequiresObservable<?> r = (RequiresObservable<?>) other;
            return observableClass.isAssignableFrom(r.getObservableClass());
        }
        return false;
    }

    @Override
    public Class<Observable<O>> contentClass() {
        return JavaTypingUtils.castClass(Observable.class);
    }

    @Override
    public String toString() {
        return "ObservableRequirement(" + PrintingUtils.quote(label) + ", " + observableClass.getSimpleName() + ")";
    }

    public FulfilledObservableRequirement<O> fulfilWith(Observable<O> observable) {
        return new FulfilledObservableRequirement<>(this, observable);
    }

}
