package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.ConstrainingComposer;
import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.componenting.system.link.ComposerComponent;
import org.processmining.estminer.specpp.componenting.system.link.CompositionComponent;
import org.processmining.estminer.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.constraints.ClinicallyOverfedPlace;
import org.processmining.estminer.specpp.datastructures.tree.constraints.ClinicallyUnderfedPlace;
import org.processmining.estminer.specpp.datastructures.util.BasicCache;
import org.processmining.estminer.specpp.evaluation.fitness.BasicFitnessEvaluation;
import org.processmining.estminer.specpp.evaluation.fitness.DetailedFitnessEvaluation;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.supervision.piping.Observable;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

public class PlaceFitnessFilter<I extends CompositionComponent<Place>, R extends Result> extends FilteringComposer<Place, I, R> implements ConstrainingComposer<Place, I, R, CandidateConstraint<Place>> {

    private final GlobalComponentRepository gcr = new GlobalComponentRepository();
    private final DelegatingEvaluator<Place, DetailedFitnessEvaluation> fitnessEvaluator = new DelegatingEvaluator<>();
    private final DelegatingDataSource<TauFitnessThresholds> fitnessThresholds = new DelegatingDataSource<>();
    private final EventSupervision<CandidateConstraint<Place>> constraintEvents = PipeWorks.eventSupervision();
    private final BasicCache<Place, DetailedFitnessEvaluation> fitnessCache;

    public PlaceFitnessFilter(ComposerComponent<Place, I, R> childComposer) {
        super(childComposer);
        fitnessCache = new BasicCache<>();
        componentSystemAdapter().require(EvaluationRequirements.DETAILED_FITNESS, fitnessEvaluator)
                                .require(ParameterRequirements.TAU_FITNESS_THRESHOLDS, fitnessThresholds)
                                .provide(SupervisionRequirements.observable("composer.constraints.under_over_fed", getPublishedConstraintClass(), getConstraintPublisher()));
        localComponentSystem()
                .provide(SupervisionRequirements.observable("composer.constraints.under_over_fed", getPublishedConstraintClass(), getConstraintPublisher()))
                .provide(DataRequirements.dataSource("fitness_cache", JavaTypingUtils.castClass(BasicCache.class), StaticDataSource.of(fitnessCache)));
    }

    @Override
    protected void initSelf() {

    }

    @Override
    public void accept(Place place) {
        DetailedFitnessEvaluation eval = fitnessEvaluator.eval(place);
        BasicFitnessEvaluation fitness = eval.getFractionalEvaluation();
        TauFitnessThresholds thresholds = fitnessThresholds.getData();
        if (fitness.getUnderfedFraction() > thresholds.getUnderfedThreshold())
            constraintEvents.observe(new ClinicallyUnderfedPlace(place));
        else if (fitness.getOverfedFraction() > thresholds.getOverfedThreshold())
            constraintEvents.observe(new ClinicallyOverfedPlace(place));
        else {
            assert fitness.getFittingFraction() >= thresholds.getFittingThreshold();
            fitnessCache.put(place, eval);
            forward(place);
        }
    }
    @Override
    public Observable<CandidateConstraint<Place>> getConstraintPublisher() {
        return constraintEvents;
    }

    @Override
    public Class<CandidateConstraint<Place>> getPublishedConstraintClass() {
        return JavaTypingUtils.castClass(CandidateConstraint.class);
    }

}
