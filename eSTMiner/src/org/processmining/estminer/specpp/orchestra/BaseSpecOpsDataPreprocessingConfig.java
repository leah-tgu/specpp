package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.data.DataSourceCollection;
import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.system.ComponentRepository;
import org.processmining.estminer.specpp.config.parameters.FitnessThresholds;
import org.processmining.estminer.specpp.datastructures.InputDataBundle;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncodings;
import org.processmining.estminer.specpp.datastructures.log.Activity;
import org.processmining.estminer.specpp.datastructures.log.Log;
import org.processmining.estminer.specpp.datastructures.log.impls.LogEncoder;
import org.processmining.estminer.specpp.datastructures.log.impls.MultiEncodedLog;
import org.processmining.estminer.specpp.datastructures.petri.Transition;

import java.util.Map;

import static org.processmining.estminer.specpp.componenting.data.StaticDataSource.of;

public class BaseSpecOpsDataPreprocessingConfig implements SpecOpsDataPreprocessingConfig {

    @Override
    public void registerDataSources(ComponentRepository cr, InputDataBundle bundle) {
        Log log = bundle.getLog();
        IntEncodings<Transition> transitionEncodings = bundle.getTransitionEncodings();
        Map<Activity, Transition> mapping = bundle.getMapping();

        DataSourceCollection dc = cr.dataSources();
        dc.register(DataRequirements.RAW_LOG, of(log));
        LogEncoder.LogEncodingParameters lep = new LogEncoder.LogEncodingParameters(false, false);
        MultiEncodedLog multiEncodedLog = LogEncoder.multiEncodeLog(log, transitionEncodings, mapping, lep);
        dc.register(DataRequirements.ENC_LOG, of(multiEncodedLog));
        dc.register(DataRequirements.CONSIDERED_VARIANTS, of(multiEncodedLog.variantIndices()));
        dc.register(DataRequirements.ENC_TRANS, of(transitionEncodings));
        dc.register(ParameterRequirements.FITNESS_THRESHOLDS, of(FitnessThresholds.exhaustive(1)));
    }

}
