package org.processmining.estminer.specpp.config.parameters;

import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.system.AbstractComponentSystemUser;
import org.processmining.estminer.specpp.componenting.traits.ProvidesParameters;
import org.processmining.estminer.specpp.config.parameters.FitnessThresholds;
import org.processmining.estminer.specpp.config.parameters.PlaceGeneratorParameters;

public class ExpansionLimitedParameters extends AbstractComponentSystemUser implements ProvidesParameters {
    public ExpansionLimitedParameters() {
        componentSystemAdapter()
                .provide(ParameterRequirements.parameters(ParameterRequirements.TAU_FITNESS_THRESHOLDS, StaticDataSource.of(TauFitnessThresholds.tau(1))))
                .provide(ParameterRequirements.parameters("placegenerator.parameters", PlaceGeneratorParameters.class,
                        StaticDataSource.of(
                                new PlaceGeneratorParameters(5, true, false, true, true))));
    }
}
