package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.base.Composer;
import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.estminer.specpp.datastructures.util.BasicCache;
import org.processmining.estminer.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.estminer.specpp.evaluation.fitness.SimplestFitnessEvaluation;
import org.processmining.estminer.specpp.evaluation.fitness.SimplifiedFitnessStatus;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

public class QueueingPostponingPlaceComposer<I extends AdvancedComposition<Place>, R extends Result, L extends CandidateConstraint<Place>> extends QueueingPostponingComposer<Place, I, R, CandidateConstraint<Place>> {

    private final DelegatingEvaluator<Place, SimplestFitnessEvaluation> fitnessEvaluator = new DelegatingEvaluator<>();
    private final DelegatingEvaluator<EvaluationParameterTuple2<Place, Integer>, DoubleScore> deltaAdaptationFunction = new DelegatingEvaluator<>();
    private final DelegatingDataSource<TauFitnessThresholds> fitnessThresholds = new DelegatingDataSource<>();
    private final DelegatingDataSource<Integer> treeLevelSource = new DelegatingDataSource<>();
    private final DelegatingDataSource<BitMask> currentlySupportedVariants = new DelegatingDataSource<>();
    private final DelegatingDataSource<BasicCache<Place, SimplestFitnessEvaluation>> fitnessCache = new DelegatingDataSource<>();

    public QueueingPostponingPlaceComposer(Composer<Place, I, R> childComposer) {
        super(childComposer);
        componentSystemAdapter().require(ParameterRequirements.TAU_FITNESS_THRESHOLDS, fitnessThresholds)
                                .require(EvaluationRequirements.SIMPLE_FITNESS, fitnessEvaluator)
                                .require(EvaluationRequirements.DELTA_ADAPTATION_FUNCTION, deltaAdaptationFunction)
                                .require(DataRequirements.dataSource("tree.current_level", Integer.class), treeLevelSource)
                                .provide(SupervisionRequirements.observable("postponing_composer.constraints", JavaTypingUtils.castClass(CandidateConstraint.class), getConstraintPublisher()));

        GlobalComponentRepository cr = new GlobalComponentRepository();
        cr.require(DataRequirements.dataSource("currently_supported_variants", BitMask.class), currentlySupportedVariants)
          .require(DataRequirements.dataSource("fitness_cache", JavaTypingUtils.castClass(BasicCache.class)), fitnessCache);
        // TODO continue
        // cr.checkout(childComposer);
    }


    @Override
    protected CandidateDecision deliberateCandidate(Place candidate) {
        SimplestFitnessEvaluation evaluation = fitnessCache.getData().getOrCompute(candidate, fitnessEvaluator);
        SimplestFitnessEvaluation fitnessEvaluation = fitnessEvaluator.eval(candidate);
        TauFitnessThresholds thresholds = fitnessThresholds.getData();
        double fraction = fitnessEvaluation.getFraction(SimplifiedFitnessStatus.FITTING);
        if (fraction < thresholds.getFittingThreshold()) {
            return CandidateDecision.Reject;
        }
        Integer treeLevel = treeLevelSource.getData();
        DoubleScore adaptedDelta = deltaAdaptationFunction.eval(new EvaluationParameterTuple2<>(candidate, treeLevel));
        double v = thresholds.getFittingThreshold() * adaptedDelta.getScore();
        // TODO continue here

        return null;
    }

    @Override
    protected CandidateDecision reDeliberateCandidate(Place candidate) {
        return null;
    }

    @Override
    protected void rejectCandidate(Place candidate) {

    }

    @Override
    protected void discardCandidate(Place candidate) {

    }


    @Override
    public Class<CandidateConstraint<Place>> getPublishedConstraintClass() {
        return JavaTypingUtils.castClass(CandidateConstraint.class);
    }
}
