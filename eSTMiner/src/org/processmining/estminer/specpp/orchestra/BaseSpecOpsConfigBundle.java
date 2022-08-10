package org.processmining.estminer.specpp.orchestra;

public class BaseSpecOpsConfigBundle extends SpecOpsConfigBundle {
    public BaseSpecOpsConfigBundle() {
        super(new BaseSpecOpsDataPreprocessingConfig(), new BaseSpecOpsComponentConfig(), new BaseSpecOpsAlgorithmParameterConfig());
    }

    @Override
    public String getTitle() {
        return "Base Version";
    }

    @Override
    public String getDescription() {
        return getTitle();
    }
}
