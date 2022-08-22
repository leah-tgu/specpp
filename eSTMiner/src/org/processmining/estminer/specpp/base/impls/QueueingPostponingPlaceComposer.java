package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.base.Composer;
import org.processmining.estminer.specpp.base.Evaluator;
import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.config.parameters.TauFitnessThresholds;
import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.MutatingSetOperations;
import org.processmining.estminer.specpp.datastructures.encoding.WeightedBitMask;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.estminer.specpp.datastructures.util.BasicCache;
import org.processmining.estminer.specpp.datastructures.util.ComputingCache;
import org.processmining.estminer.specpp.datastructures.util.EvaluationParameterTuple2;
import org.processmining.estminer.specpp.datastructures.util.StackedCache;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVector;
import org.processmining.estminer.specpp.evaluation.fitness.DetailedFitnessEvaluation;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

public class QueueingPostponingPlaceComposer<I extends AdvancedComposition<Place>, R extends Result, L extends CandidateConstraint<Place>> extends QueueingPostponingComposer<Place, I, R, CandidateConstraint<Place>> {

    private final DelegatingEvaluator<Place, DetailedFitnessEvaluation> fitnessEvaluator = new DelegatingEvaluator<>();
    private final DelegatingEvaluator<EvaluationParameterTuple2<Place, Integer>, DoubleScore> deltaAdaptationFunction = new DelegatingEvaluator<>();
    private final DelegatingDataSource<TauFitnessThresholds> fitnessThresholds = new DelegatingDataSource<>();
    private final DelegatingDataSource<WeightedBitMask> currentlySupportedVariants = new DelegatingDataSource<>();
    private final DelegatingDataSource<BasicCache<Place, DetailedFitnessEvaluation>> fitnessCache = new DelegatingDataSource<>();
    private final DelegatingDataSource<IntVector> variantFrequencies = new DelegatingDataSource<>();
    private int currentTreeLevel;
    private final DelegatingDataSource<Integer> treeLevelSource = new DelegatingDataSource<>(() -> currentTreeLevel);
    private Evaluator<Place, DetailedFitnessEvaluation> cachedEvaluator;

    public QueueingPostponingPlaceComposer(Composer<Place, I, R> childComposer) {
        super(childComposer);
        componentSystemAdapter().require(ParameterRequirements.TAU_FITNESS_THRESHOLDS, fitnessThresholds)
                                .require(EvaluationRequirements.DETAILED_FITNESS, fitnessEvaluator)
                                .require(EvaluationRequirements.DELTA_ADAPTATION_FUNCTION, deltaAdaptationFunction)
                                .require(DataRequirements.VARIANT_FREQUENCIES, variantFrequencies)
                                .require(DataRequirements.dataSource("tree.current_level", Integer.class), treeLevelSource)
                                .provide(SupervisionRequirements.observable("postponing_composer.constraints", JavaTypingUtils.castClass(CandidateConstraint.class), getConstraintPublisher()));

        currentTreeLevel = 0;
        localComponentSystem().require(DataRequirements.dataSource("currently_supported_variants", WeightedBitMask.class), currentlySupportedVariants)
                              .require(DataRequirements.dataSource("fitness_cache", JavaTypingUtils.castClass(BasicCache.class)), fitnessCache);
    }

    @Override
    public void init() {
        super.init();
        ComputingCache<Place, DetailedFitnessEvaluation> cache = new ComputingCache<>(10_000, fitnessEvaluator);
        if (fitnessCache.isEmpty()) cachedEvaluator = cache::get;
        else cachedEvaluator = new StackedCache<>(fitnessCache.getData(), cache)::get;
    }

    @Override
    protected CandidateDecision deliberateCandidate(Place candidate) {

        // TODO no
        if (candidate.size() > currentTreeLevel) {
            currentTreeLevel = candidate.size();
            handlePostponedDecisionsUntilNoChange();
        }

        DetailedFitnessEvaluation evaluation = cachedEvaluator.eval(candidate);

        /*

        // not necessary with filtering composer
        BasicFitnessEvaluation fitness = evaluation.getFractionalEvaluation();
        TauFitnessThresholds thresholds = fitnessThresholds.getData();
        if (fitness.getOverfedFraction() > thresholds.getUnderfedThreshold()) {
            publishConstraint(new ClinicallyUnderfedPlace(candidate));
            return CandidateDecision.Reject;
        } else if (fitness.getOverfedFraction() > thresholds.getOverfedThreshold()) {
            publishConstraint(new ClinicallyOverfedPlace(candidate));
            return CandidateDecision.Reject;
        }
        assert fitness.getFittingFraction() >= thresholds.getFittingThreshold();
        //
        */

        boolean meetsCurrentDelta = meetsCurrentDelta(candidate);
        if (meetsCurrentDelta) return CandidateDecision.Accept;
        else return CandidateDecision.Postpone;
    }

    @Override
    protected CandidateDecision reDeliberateCandidate(Place candidate) {
        return meetsCurrentDelta(candidate) ? CandidateDecision.Accept : CandidateDecision.Postpone;
    }

    private boolean meetsCurrentDelta(Place candidate) {
        DetailedFitnessEvaluation evaluation = cachedEvaluator.eval(candidate);
        Integer treeLevel = treeLevelSource.getData(); // more efficient: get once per postponed list traversal
        DoubleScore adaptedDelta = deltaAdaptationFunction.eval(new EvaluationParameterTuple2<>(candidate, treeLevel));
        double adaptedTau = fitnessThresholds.getData().getFittingThreshold() * adaptedDelta.getScore();
        WeightedBitMask supportedVariants = currentlySupportedVariants.getData();
        IntVector frequencies = variantFrequencies.getData();
        BitMask intersection = MutatingSetOperations.intersection(evaluation.getFittingVariants(), supportedVariants);
        double f = intersection.stream().mapToDouble(frequencies::getRelative).sum();
        return f >= supportedVariants.getWeight() - adaptedTau;
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
