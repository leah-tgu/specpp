package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Composer;
import org.processmining.estminer.specpp.base.Composition;
import org.processmining.estminer.specpp.base.ConstrainingComposer;
import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.componenting.system.LocalComponentRepository;
import org.processmining.estminer.specpp.componenting.traits.UsesGlobalComponentSystem;
import org.processmining.estminer.specpp.componenting.traits.UsesLocalComponentSystem;
import org.processmining.estminer.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.constraints.ClinicallyOverfedPlace;
import org.processmining.estminer.specpp.datastructures.tree.constraints.ClinicallyUnderfedPlace;
import org.processmining.estminer.specpp.datastructures.util.BasicCache;
import org.processmining.estminer.specpp.evaluation.fitness.SimplestFitnessEvaluation;
import org.processmining.estminer.specpp.supervision.EventSupervision;
import org.processmining.estminer.specpp.supervision.piping.Observable;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

public class PlaceFitnessFilter<I extends Composition<Place>, R extends Result> extends FilteringComposer<Place, I, R> implements ConstrainingComposer<Place, I, R, CandidateConstraint<Place>>, UsesGlobalComponentSystem, UsesLocalComponentSystem {

    private final GlobalComponentRepository gcr = new GlobalComponentRepository();
    private final LocalComponentRepository lcr = new LocalComponentRepository();
    private final DelegatingEvaluator<Place, SimplestFitnessEvaluation> fitnessEvaluator = new DelegatingEvaluator<>();
    private final DelegatingDataSource<TauFitnessThresholds> fitnessThresholds = new DelegatingDataSource<>();
    private final EventSupervision<CandidateConstraint<Place>> constraintEvents = PipeWorks.eventSupervision();
    private final BasicCache<Place, SimplestFitnessEvaluation> fitnessCache;

    public PlaceFitnessFilter(Composer<Place, I, R> childComposer) {
        super(childComposer);
        fitnessCache = new BasicCache<>();
        gcr.require(EvaluationRequirements.SIMPLE_FITNESS, fitnessEvaluator)
           .require(ParameterRequirements.TAU_FITNESS_THRESHOLDS, fitnessThresholds)
           .provide(SupervisionRequirements.observable("composer.constraints.under_over_fed", getPublishedConstraintClass(), getConstraintPublisher()));
        lcr.provide(DataRequirements.dataSource("fitness_cache", JavaTypingUtils.castClass(BasicCache.class), StaticDataSource.of(fitnessCache)));
    }

    @Override
    public void accept(Place place) {
        SimplestFitnessEvaluation fitness = fitnessEvaluator.eval(place);
        double tau = fitnessThresholds.getData().getTau();
        if (fitness.getUnderfedFraction() > 1 - tau) constraintEvents.observe(new ClinicallyUnderfedPlace(place));
        else if (fitness.getOverfedFraction() > 1 - tau) constraintEvents.observe(new ClinicallyOverfedPlace(place));
        else {
            assert fitness.getFittingFraction() >= tau;
            fitnessCache.put(place, fitness);
            forward(place);
        }
    }

    @Override
    public ComponentCollection componentSystemAdapter() {
        return gcr;
    }

    @Override
    public ComponentCollection localComponentSystem() {
        return lcr;
    }

    @Override
    public ComponentCollection getComponentCollection() {
        return gcr;
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
