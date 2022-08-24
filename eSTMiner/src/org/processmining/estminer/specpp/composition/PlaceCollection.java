package org.processmining.estminer.specpp.composition;

import org.processmining.estminer.specpp.base.Evaluator;
import org.processmining.estminer.specpp.base.impls.LightweightPlaceCollection;
import org.processmining.estminer.specpp.base.impls.PlaceCollectionLocalInfo;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.encoding.MutatingSetOperations;
import org.processmining.estminer.specpp.datastructures.encoding.WeightedBitMask;
import org.processmining.estminer.specpp.datastructures.log.impls.DenseVariantMarkingHistories;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.util.ComputingCache;
import org.processmining.estminer.specpp.datastructures.vectorization.IntVector;
import org.processmining.estminer.specpp.evaluation.implicitness.ImplicitnessRating;
import org.processmining.estminer.specpp.evaluation.implicitness.ReplayBasedImplicitnessCalculator;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

import java.util.HashMap;
import java.util.Map;

public class PlaceCollection extends LightweightPlaceCollection implements PlaceCollectionLocalInfo {
    private final Evaluator<Place, DenseVariantMarkingHistories> historyMaker;
    private final Map<Place, DenseVariantMarkingHistories> histories;
    private final Map<Place, BitMask> locallySupportedVariants;
    private WeightedBitMask currentlySupportedVariants;
    private final DelegatingDataSource<BitMask> consideredVariants = new DelegatingDataSource<>();
    private final DelegatingDataSource<IntVector> variantFrequencies = new DelegatingDataSource<>();

    private final TimeStopper timeStopper = new TimeStopper();

    public PlaceCollection() {
        histories = new HashMap<>();
        locallySupportedVariants = new HashMap<>();
        DelegatingEvaluator<Place, DenseVariantMarkingHistories> pureEvaluator = new DelegatingEvaluator<>();
        componentSystemAdapter().require(EvaluationRequirements.PLACE_MARKING_HISTORY, pureEvaluator)
                                .require(DataRequirements.CONSIDERED_VARIANTS, consideredVariants)
                                .require(DataRequirements.VARIANT_FREQUENCIES, variantFrequencies)
                                .provide(SupervisionRequirements.observable("concurrent_implicitness.performance", PerformanceEvent.class, timeStopper));
        ComputingCache<Place, DenseVariantMarkingHistories> cache = new ComputingCache<>(100, pureEvaluator);
        historyMaker = cache::get;
        localComponentSystem().provide(DataRequirements.dataSource("currently_supported_variants", WeightedBitMask.class, this::getCurrentlySupportedVariants))
                              .provide(EvaluationRequirements.PLACE_IMPLICITNESS.fulfilWith(this::rateImplicitness))
                              .provide(DataRequirements.dataSource("marking_histories_cache", JavaTypingUtils.castClass(Evaluator.class), StaticDataSource.of(cache.readOnlyGet())));
    }


    @Override
    public void initSelf() {
        resetCurrentlySupportedVariants(consideredVariants.getData());
    }

    private void resetCurrentlySupportedVariants(BitMask bm) {
        IntVector frequencies = variantFrequencies.getData();
        currentlySupportedVariants = new WeightedBitMask(bm, frequencies::getRelative);
    }

    @Override
    public void accept(Place place) {
        super.accept(place);
        DenseVariantMarkingHistories h = historyMaker.eval(place);
        histories.put(place, h);
        BitMask supportedVariants = h.computePerfectlyFitting();
        locallySupportedVariants.put(place, supportedVariants);
        currentlySupportedVariants.and(supportedVariants);
    }

    @Override
    public WeightedBitMask getCurrentlySupportedVariants() {
        return currentlySupportedVariants;
    }

    public ImplicitnessRating rateImplicitness(Place place) {
        timeStopper.start(TaskDescription.REPLAY_BASED_CONCURRENT_IMPLICITNESS);
        ImplicitnessRating implicitnessRating = ReplayBasedImplicitnessCalculator.replaySubregionImplicitness(place, historyMaker.eval(place), histories);
        timeStopper.stop(TaskDescription.REPLAY_BASED_CONCURRENT_IMPLICITNESS);
        return implicitnessRating;
    }

    @Override
    public void remove(Place candidate) {
        super.remove(candidate);
        histories.remove(candidate);
        locallySupportedVariants.remove(candidate);

        if (locallySupportedVariants.isEmpty()) {
            resetCurrentlySupportedVariants(consideredVariants.getData());
        } else {
            // TODO hella inefficient. Possibly compose result from per place info in a smarter way
            BitMask intersection = MutatingSetOperations.intersection(locallySupportedVariants.values()
                                                                                              .toArray(new BitMask[0]));
            resetCurrentlySupportedVariants(intersection);
        }
    }

}
