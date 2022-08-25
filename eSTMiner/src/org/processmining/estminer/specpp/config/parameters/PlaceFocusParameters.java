package org.processmining.estminer.specpp.config.parameters;

import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.estminer.specpp.componenting.traits.ProvidesParameters;

public class PlaceFocusParameters extends AbstractGlobalComponentSystemUser implements ProvidesParameters {
    public PlaceFocusParameters() {
        globalComponentSystem().provide(ParameterRequirements.parameters("placegenerator.parameters", PlaceGeneratorParameters.class, StaticDataSource.of(new PlaceGeneratorParameters(5, true, false, true, true))))
                               .provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWith(StaticDataSource.of(new SupervisionParameters(false))));
    }
}
