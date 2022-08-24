package org.processmining.estminer.specpp.config.parameters;

import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.estminer.specpp.componenting.traits.ProvidesParameters;

public class LightweightParameters extends AbstractGlobalComponentSystemUser implements ProvidesParameters {
    public LightweightParameters() {
        componentSystemAdapter().provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWith(StaticDataSource.of(new SupervisionParameters(false))));
    }
}
