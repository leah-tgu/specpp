package org.processmining.estminer.specpp.config.parameters;

import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.system.AbstractComponentSystemUser;
import org.processmining.estminer.specpp.componenting.system.ProvidesParameters;
import org.processmining.estminer.specpp.proposal.FitnessThresholds;

public class ParameterDefaults extends AbstractComponentSystemUser implements ProvidesParameters {

    public ParameterDefaults() {
        componentSystemAdapter().provide(ParameterRequirements.parameters(ParameterRequirements.FITNESS_THRESHOLDS, StaticDataSource.of(FitnessThresholds.exhaustive(1))))
                                .provide(ParameterRequirements.parameters("tree.tracker.parameters", TreeTrackerParameters.class, StaticDataSource.of(TreeTrackerParameters.getDefault())))
                                .provide(ParameterRequirements.parameters("placegenerator.parameters", PlaceGeneratorParameters.class, StaticDataSource.of(PlaceGeneratorParameters.getDefault())));
    }

}
