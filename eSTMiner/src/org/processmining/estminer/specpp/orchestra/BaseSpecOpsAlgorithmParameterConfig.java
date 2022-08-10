package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.componenting.data.DataSourceCollection;
import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.system.ComponentRepository;
import org.processmining.estminer.specpp.config.parameters.FitnessThresholds;
import org.processmining.estminer.specpp.config.parameters.ParameterDefaults;
import org.processmining.estminer.specpp.config.parameters.PlaceGeneratorParameters;

public class BaseSpecOpsAlgorithmParameterConfig implements SpecOpsAlgorithmParameterConfig {
    @Override
    public void registerAlgorithmParameters(ComponentRepository cr) {
        DataSourceCollection dc = cr.parameters();
        cr.absorb(new ParameterDefaults());
        dc.register(ParameterRequirements.FITNESS_THRESHOLDS, StaticDataSource.of(FitnessThresholds.tau(1)));
        dc.register(ParameterRequirements.parameters("placegenerator.parameters", PlaceGeneratorParameters.class),
                StaticDataSource.of(
                        new PlaceGeneratorParameters(Integer.MAX_VALUE, true, false, false, false)));
    }
}
