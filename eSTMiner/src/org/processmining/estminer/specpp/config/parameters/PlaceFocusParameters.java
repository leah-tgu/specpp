package org.processmining.estminer.specpp.config.parameters;

import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.system.AbstractComponentSystemUser;
import org.processmining.estminer.specpp.componenting.traits.ProvidesParameters;

public class PlaceFocusParameters extends AbstractComponentSystemUser implements ProvidesParameters {
    public PlaceFocusParameters() {
        componentSystemAdapter().provide(ParameterRequirements.parameters("placegenerator.parameters", PlaceGeneratorParameters.class, StaticDataSource.of(new PlaceGeneratorParameters(5, true, false, true, true))))
                                .provide(ParameterRequirements.parameters("supervision.parameters", SupervisionParameters.class, StaticDataSource.of(new SupervisionParameters(false))));
    }
}
