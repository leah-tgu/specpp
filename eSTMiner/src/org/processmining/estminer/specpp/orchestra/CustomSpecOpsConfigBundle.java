package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.componenting.traits.ProvidesParameters;

public class CustomSpecOpsConfigBundle extends SpecOpsConfigBundle {
    @Override
    public String getTitle() {
        return "Custom Version";
    }

    @Override
    public String getDescription() {
        return getTitle();
    }

    public CustomSpecOpsConfigBundle(ProvidesParameters customParameters) {
        super(new BaseSpecOpsDataPreprocessingConfig(), new BaseSpecOpsComponentConfig(), new AdaptedSpecOpsAlgorithmParameterConfig(customParameters));
    }
}
