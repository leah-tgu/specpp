package org.processmining.estminer.specpp.composition;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.base.impls.AbstractConstrainingComposer;
import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.constraints.AddWiredPlace;
import org.processmining.estminer.specpp.datastructures.tree.constraints.ClinicallyUnderfedPlace;
import org.processmining.estminer.specpp.datastructures.tree.constraints.RemoveWiredPlace;
import org.processmining.estminer.specpp.evaluation.fitness.SimplestFitnessEvaluation;
import org.processmining.estminer.specpp.evaluation.fitness.SimplifiedFitnessStatus;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;

/**
 * The base implementation of a {@code Composer} for candidates of type {@code Place}.
 * This class participates in the componenting system to require a place fitness evaluator and fitness thresholds to base its {@code deliberateAcceptance(Place)} on.
 * Further, it provides {@code PerformanceEvent} measurements of its decision-making process of accepting or rejecting a candidate place.
 * It internally employs a mutable composition object which in turn allows it to retroactively revoke acceptance of a candidate. (<it>this is used in implicit place removal</it>)
 * It publishes constraints when places are accepted, their acceptance is revoked and when a place meets the {@code FitnessThresholds} child pruning threshold.
 *
 * @param <I> the type of the internally used {@code Composition}
 * @see AbstractConstrainingComposer
 */
public class PlacesComposer<I extends AdvancedComposition<Place>> extends AbstractConstrainingComposer<Place, I, PetriNet> {


    protected final DelegatingEvaluator<Place, SimplestFitnessEvaluation> fitnessEvaluator = new DelegatingEvaluator<>();

    protected final DelegatingDataSource<TauFitnessThresholds> fitnessThresholds = new DelegatingDataSource<>();

    protected final TimeStopper timeStopper = new TimeStopper();

    public PlacesComposer(I placeComposition) {
        super(placeComposition, c -> new PetriNet(c.toSet()));
        componentSystemAdapter().require(ParameterRequirements.TAU_FITNESS_THRESHOLDS, fitnessThresholds)
                                .require(EvaluationRequirements.SIMPLE_FITNESS, fitnessEvaluator)
                                .provide(SupervisionRequirements.observable("composer.performance", PerformanceEvent.class, timeStopper));
    }

    @Override
    public void accept(Place candidate) {
        timeStopper.start(TaskDescription.CANDIDATE_COMPOSITION);
        super.accept(candidate);
        timeStopper.stop(TaskDescription.CANDIDATE_COMPOSITION);
    }

    @Override
    protected boolean deliberateAcceptance(Place candidate) {
        SimplestFitnessEvaluation fitness = fitnessEvaluator.eval(candidate);
        if (isSufficientlyUnderfed(fitness)) {
            publishConstraint(new ClinicallyUnderfedPlace(candidate));
            return false;
        } else return isSufficientlyFitting(fitness);
    }

    protected boolean isSufficientlyFitting(SimplestFitnessEvaluation fitness) {
        return fitness.getFraction(SimplifiedFitnessStatus.FITTING) >= fitnessThresholds.getData().getFittingThreshold();
    }

    protected boolean isSufficientlyUnderfed(SimplestFitnessEvaluation fitness) {
        return fitness.getFraction(SimplifiedFitnessStatus.UNDERFED) > fitnessThresholds.getData().getUnderfedThreshold();
    }

    @Override
    protected void candidateAccepted(Place candidate) {
        publishConstraint(new AddWiredPlace(candidate));
    }

    @Override
    protected void candidateRejected(Place candidate) {

    }

    @Override
    protected void acceptanceRevoked(Place candidate) {
        publishConstraint(new RemoveWiredPlace(candidate));
    }


}
