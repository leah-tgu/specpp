package org.processmining.estminer.specpp.orchestra;

public class LightweightSpecOpsConfigBundle extends SpecOpsConfigBundle {
    public LightweightSpecOpsConfigBundle() {
        super(new BaseSpecOpsDataPreprocessingConfig(), new LightweightSpecOpsComponentConfig(), new BaseSpecOpsAlgorithmParameterConfig());
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
