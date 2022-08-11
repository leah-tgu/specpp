package org.processmining.estminer.specpp.componenting.system;

import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.traits.ProvidesParameters;
import org.processmining.estminer.specpp.config.parameters.FitnessThresholds;
import org.processmining.estminer.specpp.config.parameters.PlaceGeneratorParameters;

public class ExpansionLimitedParameters extends AbstractComponentSystemUser implements ProvidesParameters {
    public ExpansionLimitedParameters() {
        componentSystemAdapter()
                .provide(ParameterRequirements.parameters(ParameterRequirements.FITNESS_THRESHOLDS, StaticDataSource.of(FitnessThresholds.tau(1))))
                .provide(ParameterRequirements.parameters("placegenerator.parameters", PlaceGeneratorParameters.class,
                        StaticDataSource.of(
                                new PlaceGeneratorParameters(6, true, false, true, true))));
    }
}
