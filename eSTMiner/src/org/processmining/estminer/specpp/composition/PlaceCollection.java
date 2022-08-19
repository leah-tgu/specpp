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
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.componenting.system.LocalComponentRepository;
import org.processmining.estminer.specpp.componenting.traits.UsesGlobalComponentSystem;
import org.processmining.estminer.specpp.componenting.traits.UsesLocalComponentSystem;
import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.log.impls.DenseVariantMarkingHistories;
import org.processmining.estminer.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.util.ComputingCache;
import org.processmining.estminer.specpp.evaluation.implicitness.ImplicitnessRating;
import org.processmining.estminer.specpp.evaluation.implicitness.ReplayBasedImplicitnessCalculator;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

import java.util.HashMap;
import java.util.Map;

public class PlaceCollection extends LightweightPlaceCollection implements PlaceCollectionLocalInfo, UsesGlobalComponentSystem, UsesLocalComponentSystem {
    private final Evaluator<Place, DenseVariantMarkingHistories> historyMaker;
    private final Map<Place, DenseVariantMarkingHistories> histories;
    private final Map<Place, BitMask> supportedVariantsBeforePlaceAddition;
    private BitMask currentlySupportedVariants;
    private final DelegatingDataSource<MultiEncodedLog> multiEncodedLog = new DelegatingDataSource<>();

    private final GlobalComponentRepository gcr = new GlobalComponentRepository();
    private final LocalComponentRepository lcr = new LocalComponentRepository();
    private final TimeStopper timeStopper = new TimeStopper();

    public PlaceCollection() {
        histories = new HashMap<>();
        supportedVariantsBeforePlaceAddition = new HashMap<>();
        DelegatingEvaluator<Place, DenseVariantMarkingHistories> pureEvaluator = new DelegatingEvaluator<>();
        gcr.require(EvaluationRequirements.PLACE_MARKING_HISTORY, pureEvaluator)
           .require(DataRequirements.ENC_LOG, multiEncodedLog)
           .provide(SupervisionRequirements.observable("concurrent_implicitness.performance", PerformanceEvent.class, timeStopper));
        ComputingCache<Place, DenseVariantMarkingHistories> cache = new ComputingCache<>(100, pureEvaluator);
        historyMaker = cache::get;
        lcr.provide(DataRequirements.dataSource("currently_supported_variants", BitMask.class, this::getCurrentSupportedVariants))
           .provide(EvaluationRequirements.PLACE_IMPLICITNESS.fulfilWith(this::rateImplicitness))
           .provide(DataRequirements.dataSource("marking_histories_cache", JavaTypingUtils.castClass(Evaluator.class), StaticDataSource.of(cache.readOnlyGet())));
    }

    @Override
    public void accept(Place place) {
        super.accept(place);
        DenseVariantMarkingHistories h = historyMaker.eval(place);
        histories.put(place, h);
        if (currentlySupportedVariants == null) {
            currentlySupportedVariants = getFullVariantBitmask();
        }
        BitMask supportedVariants = h.computePerfectlyFitting();
        supportedVariantsBeforePlaceAddition.put(place, currentlySupportedVariants.copy());
        currentlySupportedVariants.and(supportedVariants);
    }

    private BitMask getFullVariantBitmask() {
        return multiEncodedLog.getData().variantIndices().copy();
    }

    @Override
    public BitMask getCurrentSupportedVariants() {
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
        // TODO recalculate properly
        BitMask supportedBeforeCandidate = supportedVariantsBeforePlaceAddition.remove(candidate);
        currentlySupportedVariants.and(supportedBeforeCandidate);
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
}
