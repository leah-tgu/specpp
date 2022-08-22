package org.processmining.estminer.specpp.composition;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.base.impls.AbstractConstrainingComposer;
import org.processmining.estminer.specpp.base.impls.CandidateConstraint;
import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.traits.UsesLocalComponentSystem;
import org.processmining.estminer.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.constraints.AddWiredPlace;
import org.processmining.estminer.specpp.datastructures.tree.constraints.ClinicallyUnderfedPlace;
import org.processmining.estminer.specpp.datastructures.tree.constraints.RemoveWiredPlace;
import org.processmining.estminer.specpp.evaluation.fitness.BasicFitnessEvaluation;
import org.processmining.estminer.specpp.evaluation.fitness.BasicFitnessStatus;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

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
public class PlacesComposer<I extends AdvancedComposition<Place>> extends AbstractConstrainingComposer<Place, I, PetriNet, CandidateConstraint<Place>> {


    protected final DelegatingEvaluator<Place, BasicFitnessEvaluation> fitnessEvaluator = new DelegatingEvaluator<>();

    protected final DelegatingDataSource<TauFitnessThresholds> fitnessThresholds = new DelegatingDataSource<>();

    public PlacesComposer(I placeComposition) {
        super(placeComposition, c -> new PetriNet(c.toSet()));
        componentSystemAdapter().require(ParameterRequirements.TAU_FITNESS_THRESHOLDS, fitnessThresholds)
                                .require(EvaluationRequirements.BASIC_FITNESS, fitnessEvaluator)
                                .provide(SupervisionRequirements.observable("composer.constraints.wiring", getPublishedConstraintClass(), getConstraintPublisher()));
        localComponentSystem().provide(SupervisionRequirements.observable("composer.constraints.wiring", getPublishedConstraintClass(), getConstraintPublisher()));
    }

    @Override
    public void init() {
        UsesLocalComponentSystem.bridgeTheGap(this, composition);
        super.init();
    }

    @Override
    protected boolean deliberateAcceptance(Place candidate) {
        BasicFitnessEvaluation fitness = fitnessEvaluator.eval(candidate);
        if (isSufficientlyUnderfed(fitness)) {
            publishConstraint(new ClinicallyUnderfedPlace(candidate));
            return false;
        } else return isSufficientlyFitting(fitness);
    }

    protected boolean isSufficientlyFitting(BasicFitnessEvaluation fitness) {
        return fitness.getFraction(BasicFitnessStatus.FITTING) >= fitnessThresholds.getData()
                                                                                   .getFittingThreshold();
    }

    protected boolean isSufficientlyUnderfed(BasicFitnessEvaluation fitness) {
        return fitness.getFraction(BasicFitnessStatus.UNDERFED) > fitnessThresholds.getData()
                                                                                   .getUnderfedThreshold();
    }

    @Override
    public Class<CandidateConstraint<Place>> getPublishedConstraintClass() {
        return JavaTypingUtils.castClass(CandidateConstraint.class);
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
