package org.processmining.estminer.specpp.composition;

import org.processmining.estminer.specpp.base.MutableCappedComposition;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.estminer.specpp.componenting.traits.UsesComponentSystem;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.constraints.ClinicallyUnderfedPlace;
import org.processmining.estminer.specpp.evaluation.fitness.AggregatedBasicFitnessEvaluation;
import org.processmining.estminer.specpp.evaluation.implicitness.*;

/**
 * This class extends the base {@code PlaceComposer} with concurrent implicit place removal.
 * It additionally requires an {@code implicitnessEvaluator} that calculates an {@code ImplicitnessRating} to decide whether to accept or reject a new candidate, or whether to replace an existing, i.e., previously accepted, candidate.
 *
 * @param <I> the type of the internally used {@code Composition}
 * @see PlacesComposer
 * @see ImplicitnessRating
 * @see EvaluationRequirements#PLACE_IMPLICITNESS
 */
public class PlaceComposerWithConcurrentImplicitnessTesting<I extends MutableCappedComposition<Place>> extends PlacesComposer<I> {

    protected final DelegatingEvaluator<Place, ImplicitnessRating> implicitnessEvaluator = new DelegatingEvaluator<>(p -> BooleanImplicitness.NOT_IMPLICIT);

    public PlaceComposerWithConcurrentImplicitnessTesting(I placeComposition) {
        super(placeComposition);
        componentSystemAdapter().require(EvaluationRequirements.PLACE_IMPLICITNESS, implicitnessEvaluator);
        if (placeComposition instanceof ProvidesEvaluators)
            componentSystemAdapter().fulfilFrom(((UsesComponentSystem) placeComposition).componentSystemAdapter());
    }

    @Override
    protected boolean deliberateAcceptance(Place candidate) {
        AggregatedBasicFitnessEvaluation fitness = fitnessEvaluator.eval(candidate);
        if (meetsUnderfedThreshold(fitness)) {
            publishConstraint(new ClinicallyUnderfedPlace(candidate));
        } else if (meetsFitnessThreshold(fitness)) {
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
        }
        return false;
    }


}
