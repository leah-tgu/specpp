package org.processmining.estminer.specpp.config.parameters;

import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.estminer.specpp.componenting.traits.ProvidesParameters;

public class ExhaustiveParameters extends AbstractGlobalComponentSystemUser implements ProvidesParameters {

    public ExhaustiveParameters() {
        componentSystemAdapter().provide(ParameterRequirements.parameters(ParameterRequirements.TAU_FITNESS_THRESHOLDS, StaticDataSource.of(TauFitnessThresholds.tau(1))))
                                .provide(ParameterRequirements.parameters(ParameterRequirements.PLACE_GENERATOR_PARAMETERS, StaticDataSource.of(PlaceGeneratorParameters.getDefault())));
    }

}
