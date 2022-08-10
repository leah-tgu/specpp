package org.processmining.estminer.specpp.orchestra;

public class ExpansiveSpecOpsConfigBundle extends SpecOpsConfigBundle {
    @Override
    public String getTitle() {
        return "Fully Instrumented Version";
    }

    @Override
    public String getDescription() {
        return getTitle();
    }

    public ExpansiveSpecOpsConfigBundle(){
        super(new BaseSpecOpsDataPreprocessingConfig(), new ExpansiveSpecOpsComponentConfig(), new BaseSpecOpsAlgorithmParameterConfig());
    }
}
