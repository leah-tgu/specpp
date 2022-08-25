package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.config.parameters.UniwiredParameters;

public class UniwiredConfigBundle extends SpecOpsConfigBundle {
    @Override
    public String getTitle() {
        return "Uniwired Variant";
    }

    @Override
    public String getDescription() {
        return getTitle();
    }

    public UniwiredConfigBundle() {
        super(new BaseSpecOpsDataPreprocessingConfig(), new UniwiredComponentConfig(), new AdaptedSpecOpsAlgorithmParameterConfig(new UniwiredParameters()));
    }
}
