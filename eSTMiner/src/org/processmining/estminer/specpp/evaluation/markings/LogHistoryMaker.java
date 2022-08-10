package org.processmining.estminer.specpp.evaluation.markings;

import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.system.AbstractComponentSystemUser;
import org.processmining.estminer.specpp.componenting.traits.IsGlobalProvider;
import org.processmining.estminer.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.estminer.specpp.datastructures.BitMask;
import org.processmining.estminer.specpp.datastructures.log.impls.DenseVariantMarkingHistories;
import org.processmining.estminer.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.util.ComputingCache;

public class LogHistoryMaker extends AbstractComponentSystemUser implements ProvidesEvaluators, IsGlobalProvider {

    private final DelegatingDataSource<MultiEncodedLog> encodedLogSource = DataRequirements.ENC_LOG.emptyDelegator();
    private final DelegatingDataSource<BitMask> consideredVariantsSource = DataRequirements.CONSIDERED_VARIANTS.emptyDelegator();

    private BitMask consideredVariants;

    public LogHistoryMaker() {
        componentSystemAdapter().require(DataRequirements.ENC_LOG, encodedLogSource)
                                .require(DataRequirements.CONSIDERED_VARIANTS, consideredVariantsSource)
                                .provide(EvaluationRequirements.evaluator(EvaluationRequirements.PLACE_MARKING_HISTORY, this::computeVariantMarkingHistories));
    }

    protected void updateConsideredVariants() {
        setConsideredVariants(consideredVariantsSource.getData());
    }

    public DenseVariantMarkingHistories computeVariantMarkingHistories(Place input) {
        updateConsideredVariants();
        return consideredVariantsSource.isSet() ? QuickReplay.makeHistoryOn(consideredVariants, encodedLogSource.getData(), input) : QuickReplay.makeHistory(encodedLogSource.getData(), input);
    }

    public BitMask getConsideredVariants() {
        return consideredVariants;
    }

    public void setConsideredVariants(BitMask consideredVariants) {
        this.consideredVariants = consideredVariants;
    }

    @Override
    public String toString() {
        return "LogHistoryMaker()";
    }
}
