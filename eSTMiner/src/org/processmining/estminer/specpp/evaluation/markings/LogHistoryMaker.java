package org.processmining.estminer.specpp.evaluation.markings;

import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.estminer.specpp.componenting.traits.IsGlobalProvider;
import org.processmining.estminer.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.log.impls.DenseVariantMarkingHistories;
import org.processmining.estminer.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.estminer.specpp.datastructures.petri.Place;

public class LogHistoryMaker extends AbstractGlobalComponentSystemUser implements ProvidesEvaluators, IsGlobalProvider {

    private final DelegatingDataSource<MultiEncodedLog> encodedLogSource = new DelegatingDataSource<>();
    private final DelegatingDataSource<BitMask> consideredVariantsSource = new DelegatingDataSource<>();

    private BitMask consideredVariants;

    public LogHistoryMaker() {
        globalComponentSystem().require(DataRequirements.ENC_LOG, encodedLogSource)
                               .require(DataRequirements.CONSIDERED_VARIANTS, consideredVariantsSource)
                               .provide(EvaluationRequirements.PLACE_MARKING_HISTORY.fulfilWith(this::computeVariantMarkingHistories));
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
