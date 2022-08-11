package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.componenting.system.ExpansionLimitedParameters;

public class LightweightSpecOpsConfigBundle extends SpecOpsConfigBundle {
    public LightweightSpecOpsConfigBundle() {
        super(new BaseSpecOpsDataPreprocessingConfig(), new LightweightSpecOpsComponentConfig(), new AdaptedSpecOpsAlgorithmParameterConfig(new ExpansionLimitedParameters()));
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
