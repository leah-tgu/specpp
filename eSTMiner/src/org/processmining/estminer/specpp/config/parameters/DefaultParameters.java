package org.processmining.estminer.specpp.config.parameters;

import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.system.AbstractComponentSystemUser;
import org.processmining.estminer.specpp.componenting.traits.ProvidesParameters;

import static org.processmining.estminer.specpp.componenting.data.ParameterRequirements.*;

public class DefaultParameters extends AbstractComponentSystemUser implements ProvidesParameters {

    public DefaultParameters() {
        componentSystemAdapter().provide(ParameterRequirements.parameters(OUTPUT_PATH_PARAMETERS, StaticDataSource.of(OutputPathParameters.getDefault())))
                                .provide(ParameterRequirements.parameters("supervision.parameters", SupervisionParameters.class, StaticDataSource.of(SupervisionParameters.getDefault())))
                                .provide(ParameterRequirements.parameters(TAU_FITNESS_THRESHOLDS, StaticDataSource.of(TauFitnessThresholds.getDefault())))
                                .provide(ParameterRequirements.parameters("tree.tracker.parameters", TreeTrackerParameters.class, StaticDataSource.of(TreeTrackerParameters.getDefault())))
                                .provide(ParameterRequirements.parameters(PLACE_GENERATOR_PARAMETERS, StaticDataSource.of(PlaceGeneratorParameters.getDefault())));
    }

}
