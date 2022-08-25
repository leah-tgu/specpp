package org.processmining.estminer.specpp.composition;

import org.processmining.estminer.specpp.base.Constrainer;
import org.processmining.estminer.specpp.base.impls.CandidateConstraint;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.constraints.AddWiredPlace;
import org.processmining.estminer.specpp.datastructures.tree.constraints.RemoveWiredPlace;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.supervision.piping.Observable;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

public class ConstrainingPlaceCollection extends PlaceCollection implements Constrainer<CandidateConstraint<Place>> {

    private final EventSupervision<CandidateConstraint<Place>> constraintOutput = PipeWorks.eventSupervision();

    public ConstrainingPlaceCollection() {
        globalComponentSystem().provide(SupervisionRequirements.observable("composition.constraints.wiring", getPublishedConstraintClass(), getConstraintPublisher()));
        localComponentSystem().provide(SupervisionRequirements.observable("composition.constraints.wiring", getPublishedConstraintClass(), getConstraintPublisher()));
    }

    @Override
    public void accept(Place place) {
        super.accept(place);
        constraintOutput.observe(new AddWiredPlace(place));
    }

    @Override
    public void remove(Place candidate) {
        super.remove(candidate);
        constraintOutput.observe(new RemoveWiredPlace(candidate));
    }

    @Override
    public Observable<CandidateConstraint<Place>> getConstraintPublisher() {
        return constraintOutput;
    }

    @Override
    public Class<CandidateConstraint<Place>> getPublishedConstraintClass() {
        return JavaTypingUtils.castClass(CandidateConstraint.class);
    }

}
