package org.processmining.estminer.specpp.config.parameters;

import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;

public class LightweightExpansionLimitedParameters extends ExpansionLimitedParameters {
    public LightweightExpansionLimitedParameters() {
        componentSystemAdapter().provide(ParameterRequirements.parameters("supervision.parameters", SupervisionParameters.class, StaticDataSource.of(new SupervisionParameters(false))));
    }
}
