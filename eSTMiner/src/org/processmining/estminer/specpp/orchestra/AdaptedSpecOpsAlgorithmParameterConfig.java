package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.componenting.system.ComponentRepository;
import org.processmining.estminer.specpp.componenting.traits.ProvidesParameters;

public class AdaptedSpecOpsAlgorithmParameterConfig implements SpecOpsAlgorithmParameterConfig {

    private final ProvidesParameters parameters;

    public AdaptedSpecOpsAlgorithmParameterConfig(ProvidesParameters changedParameters) {
        this.parameters = changedParameters;
    }

    @Override
    public void registerAlgorithmParameters(ComponentRepository cr) {
        SpecOpsAlgorithmParameterConfig.super.registerAlgorithmParameters(cr);
        cr.overridingAbsorb(parameters);
    }
}
