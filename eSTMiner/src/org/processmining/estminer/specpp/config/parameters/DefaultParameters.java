package org.processmining.estminer.specpp.config.parameters;

import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.estminer.specpp.componenting.traits.ProvidesParameters;

public class DefaultParameters extends AbstractGlobalComponentSystemUser implements ProvidesParameters {

    public DefaultParameters() {
        componentSystemAdapter().provide(ParameterRequirements.OUTPUT_PATH_PARAMETERS.fulfilWith(StaticDataSource.of(OutputPathParameters.getDefault())))
                                .provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWith(StaticDataSource.of(SupervisionParameters.getDefault())))
                                .provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWith(StaticDataSource.of(TauFitnessThresholds.getDefault())))
                                .provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWith(StaticDataSource.of(PlaceGeneratorParameters.getDefault())));
    }

}
