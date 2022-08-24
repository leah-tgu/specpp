package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.config.parameters.TauDeltaParameters;

public class TauDeltaConfigBundle extends SpecOpsConfigBundle {
    @Override
    public String getTitle() {
        return "Tau Delta Variant";
    }

    @Override
    public String getDescription() {
        return getTitle();
    }

    public TauDeltaConfigBundle() {
        super(new BaseSpecOpsDataPreprocessingConfig(), new TauDeltaComponentConfig(), new AdaptedSpecOpsAlgorithmParameterConfig(new TauDeltaParameters()));
    }
}
