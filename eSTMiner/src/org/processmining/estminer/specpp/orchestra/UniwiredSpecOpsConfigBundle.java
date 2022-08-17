package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.config.parameters.UniwiredParameters;

public class UniwiredSpecOpsConfigBundle extends SpecOpsConfigBundle {
    @Override
    public String getTitle() {
        return "Uniwired Variant";
    }

    @Override
    public String getDescription() {
        return getTitle();
    }

    public UniwiredSpecOpsConfigBundle() {
        super(new BaseSpecOpsDataPreprocessingConfig(), new UniwiredSpecOpsComponentConfig(), new AdaptedSpecOpsAlgorithmParameterConfig(new UniwiredParameters()));
    }
}
