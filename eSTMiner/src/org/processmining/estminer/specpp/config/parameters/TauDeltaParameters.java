package org.processmining.estminer.specpp.config.parameters;

import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.estminer.specpp.componenting.traits.ProvidesParameters;

public class TauDeltaParameters extends AbstractGlobalComponentSystemUser implements ProvidesParameters {

    public TauDeltaParameters() {
        componentSystemAdapter().provide(ParameterRequirements.DELTA_PARAMETERS.fulfilWith(StaticDataSource.of(new DeltaParameters(1))));
    }

}
