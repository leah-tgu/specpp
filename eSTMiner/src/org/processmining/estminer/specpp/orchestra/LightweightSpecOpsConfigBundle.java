package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.config.parameters.ExpansionLimitedParameters;
import org.processmining.estminer.specpp.config.parameters.LightweightExpansionLimitedParameters;

public class LightweightSpecOpsConfigBundle extends SpecOpsConfigBundle {
    public LightweightSpecOpsConfigBundle() {
        super(new BaseSpecOpsDataPreprocessingConfig(), new LightweightSpecOpsComponentConfig(), new AdaptedSpecOpsAlgorithmParameterConfig(new LightweightExpansionLimitedParameters()));
    }

    @Override
    public String getTitle() {
        return "Lightweight Version";
    }

    @Override
    public String getDescription() {
        return getTitle();
    }
}
