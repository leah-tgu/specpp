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
import org.processmining.estminer.specpp.supervision.supervisors.DebuggingSupervisor;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PlaceCollection extends LightweightPlaceCollection implements PlaceCollectionLocalInfo {
    protected final Evaluator<Place, DenseVariantMarkingHistories> historyMaker;
    protected final Map<Place, DenseVariantMarkingHistories> histories;
    protected final Map<Place, BitMask> locallySupportedVariants;
    protected WeightedBitMask currentlySupportedVariants;
    protected final DelegatingDataSource<BitMask> consideredVariants = new DelegatingDataSource<>();
    protected final DelegatingDataSource<IntVector> variantFrequencies = new DelegatingDataSource<>();

    protected final TimeStopper timeStopper = new TimeStopper();

    public PlaceCollection() {
        histories = new HashMap<>();
        locallySupportedVariants = new HashMap<>();
        DelegatingEvaluator<Place, DenseVariantMarkingHistories> pureEvaluator = new DelegatingEvaluator<>();
        globalComponentSystem().require(EvaluationRequirements.PLACE_MARKING_HISTORY, pureEvaluator)
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
        DebuggingSupervisor.debug("supported variants addition-before", currentlySupportedVariants);
        DebuggingSupervisor.debug("supported variants addition-to_add", supportedVariants);
        currentlySupportedVariants.intersection(supportedVariants);
        DebuggingSupervisor.debug("supported variants addition-after", currentlySupportedVariants);
    }

    @Override
    public WeightedBitMask getCurrentlySupportedVariants() {
        return currentlySupportedVariants;
    }

    public ImplicitnessRating rateImplicitness(Place place) {
        timeStopper.start(TaskDescription.REPLAY_BASED_CONCURRENT_IMPLICITNESS);
        DenseVariantMarkingHistories h = historyMaker.eval(place);
        ImplicitnessRating implicitnessRating = ReplayBasedImplicitnessCalculator.replaySubregionImplicitness(place, h, histories);
        timeStopper.stop(TaskDescription.REPLAY_BASED_CONCURRENT_IMPLICITNESS);
        return implicitnessRating;
    }

    @Override
    public void remove(Place candidate) {
        super.remove(candidate);
        histories.remove(candidate);
        locallySupportedVariants.remove(candidate);

        DebuggingSupervisor.debug("supported variants removal-before", currentlySupportedVariants);
        if (locallySupportedVariants.isEmpty()) {
            resetCurrentlySupportedVariants(consideredVariants.getData());
        } else {
            // TODO hella inefficient. Possibly compose result from per place info in a smarter way
            Iterator<BitMask> iterator = locallySupportedVariants.values().iterator();
            BitMask result = iterator.next().copy();
            while (iterator.hasNext()) {
                BitMask next = iterator.next();
                result.intersection(next);
            }
            resetCurrentlySupportedVariants(result);
        }
        DebuggingSupervisor.debug("supported variants removal-after", currentlySupportedVariants);
    }

}
