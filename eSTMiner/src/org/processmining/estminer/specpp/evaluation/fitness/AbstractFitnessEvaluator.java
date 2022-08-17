package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.data.DataSource;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.AbstractComponentSystemUser;
import org.processmining.estminer.specpp.componenting.traits.IsGlobalProvider;
import org.processmining.estminer.specpp.componenting.traits.ProvidesEvaluators;
import org.processmining.estminer.specpp.datastructures.encoding.BitMask;
import org.processmining.estminer.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.piping.TimeStopper;

public abstract class AbstractFitnessEvaluator extends AbstractComponentSystemUser implements ProvidesEvaluators, IsGlobalProvider {

    private final DelegatingDataSource<MultiEncodedLog> multiEncodedLogSource = new DelegatingDataSource<>();
    private final DelegatingDataSource<BitMask> variantSubsetSource = new DelegatingDataSource<>();

    protected final TimeStopper timeStopper = new TimeStopper();
    private BitMask consideredVariants;

    public AbstractFitnessEvaluator(DataSource<MultiEncodedLog> multiEncodedLogDataSource, DataSource<BitMask> variantSubsetSource) {
        this.multiEncodedLogSource.setDelegate(multiEncodedLogDataSource);
        this.variantSubsetSource.setDelegate(variantSubsetSource);
    }

    public AbstractFitnessEvaluator() {
        componentSystemAdapter().require(DataRequirements.CONSIDERED_VARIANTS, variantSubsetSource)
                                .require(DataRequirements.ENC_LOG, multiEncodedLogSource)
                                .provide(SupervisionRequirements.observable("evaluator.performance", PerformanceEvent.class, timeStopper));
    }

    public void updateConsideredVariants() {
        setConsideredVariants(variantSubsetSource.getData());
    }

    public BitMask getConsideredVariants() {
        return consideredVariants;
    }

    protected void setConsideredVariants(BitMask consideredVariants) {
        this.consideredVariants = consideredVariants;
    }


    public MultiEncodedLog getMultiEncodedLog() {
        return multiEncodedLogSource.getData();
    }

}
