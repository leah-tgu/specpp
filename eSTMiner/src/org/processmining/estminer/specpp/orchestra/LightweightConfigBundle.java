package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.config.parameters.LightweightParameters;

public class LightweightConfigBundle extends SpecOpsConfigBundle {
    public LightweightConfigBundle() {
        super(new BaseSpecOpsDataPreprocessingConfig(), new LightweightComponentConfig(), new AdaptedSpecOpsAlgorithmParameterConfig(new LightweightParameters()));
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
