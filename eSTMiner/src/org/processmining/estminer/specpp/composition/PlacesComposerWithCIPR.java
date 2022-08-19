package org.processmining.estminer.specpp.composition;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.system.LocalComponentRepository;
import org.processmining.estminer.specpp.componenting.traits.UsesLocalComponentSystem;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.constraints.ClinicallyUnderfedPlace;
import org.processmining.estminer.specpp.evaluation.fitness.SimplestFitnessEvaluation;
import org.processmining.estminer.specpp.evaluation.implicitness.*;

/**
 * This class extends the base {@code PlaceComposer} with concurrent implicit place removal.
 * It additionally requires an {@code implicitnessEvaluator} that calculates an {@code ImplicitnessRating} to decide whether to accept or reject a new candidate, or whether to replace an existing, i.e., previously accepted, candidate.
 * This requirement is fulfilled locally by the given place composition.
 *
 * @param <I> the type of the internally used {@code Composition}
 * @see PlacesComposer
 * @see ImplicitnessRating
 * @see EvaluationRequirements#PLACE_IMPLICITNESS
 * @see PlaceCollection
 */
public class PlacesComposerWithCIPR<I extends AdvancedComposition<Place>> extends PlacesComposer<I> implements UsesLocalComponentSystem {

    protected final LocalComponentRepository lcr = new LocalComponentRepository();
    protected final DelegatingEvaluator<Place, ImplicitnessRating> implicitnessEvaluator = new DelegatingEvaluator<>(p -> BooleanImplicitness.NOT_IMPLICIT);

    public PlacesComposerWithCIPR(I placeComposition) {
        super(placeComposition);
        lcr.require(EvaluationRequirements.PLACE_IMPLICITNESS, implicitnessEvaluator);
        if (placeComposition instanceof UsesLocalComponentSystem) {
            ComponentCollection other = ((UsesLocalComponentSystem) placeComposition).localComponentSystem();
            lcr.fulfilFrom(other);
            lcr.fulfil(other);
        }
    }

    @Override
    public ComponentCollection localComponentSystem() {
        return lcr;
    }

    @Override
    protected boolean deliberateAcceptance(Place candidate) {
        SimplestFitnessEvaluation fitness = fitnessEvaluator.eval(candidate);
        if (isSufficientlyUnderfed(fitness)) {
            publishConstraint(new ClinicallyUnderfedPlace(candidate));
            return false;
        } else if (isSufficientlyFitting(fitness)) {
            ImplicitnessRating rating = implicitnessEvaluator.eval(candidate);
            if (rating instanceof ReplaceExaminedPlace) {
                return false;
            } else if (rating instanceof ReplaceExistingPlace) {
                Place p1 = ((ReplaceExistingPlace) rating).getCandidate();
                Place p2 = ((ReplaceExistingPlace) rating).getExisting();
                Place p3 = ((ReplaceExistingPlace) rating).getReplacement();
                revokeAcceptance(p2);
                //acceptCandidate(p3);
                return true;
            } else if (rating instanceof ReplacementPlaceInfeasible) return true;
            else if (rating instanceof BooleanImplicitness) return rating == BooleanImplicitness.NOT_IMPLICIT;
            else return false;
        } else return false;
    }


}
