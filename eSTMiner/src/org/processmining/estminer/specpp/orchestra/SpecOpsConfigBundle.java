package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.componenting.system.ComponentRepository;
import org.processmining.estminer.specpp.datastructures.InputDataBundle;

public class SpecOpsConfigBundle {

    private final SpecOpsDataPreprocessingConfig dataPreprocessingConfig;
    private final SpecOpsComponentConfig componentConfig;
    private final SpecOpsAlgorithmParameterConfig algorithmParameterConfig;


    public SpecOpsConfigBundle(SpecOpsDataPreprocessingConfig dataPreprocessingConfig, SpecOpsComponentConfig componentConfig, SpecOpsAlgorithmParameterConfig algorithmParameterConfig) {
        this.dataPreprocessingConfig = dataPreprocessingConfig;
        this.componentConfig = componentConfig;
        this.algorithmParameterConfig = algorithmParameterConfig;
    }

    public void instantiate(ComponentRepository cr, InputDataBundle bundle) {
        dataPreprocessingConfig.registerDataSources(cr, bundle);
        componentConfig.registerConfigurations(cr);
        algorithmParameterConfig.registerAlgorithmParameters(cr);
    }

}
