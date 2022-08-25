package org.processmining.estminer.specpp.config.parameters;

import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;

public class LightweightExpansionLimitedParameters extends ExpansionLimitedParameters {
    public LightweightExpansionLimitedParameters() {
        globalComponentSystem().provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWith(StaticDataSource.of(new SupervisionParameters(false))));
    }
}
