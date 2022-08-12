package org.processmining.estminer.specpp.componenting.data;

import org.processmining.estminer.specpp.config.parameters.FitnessThresholds;
import org.processmining.estminer.specpp.config.parameters.Parameters;
import org.processmining.estminer.specpp.config.parameters.TauFitnessThresholds;

public class ParameterRequirements {

    public static final ParameterRequirement<FitnessThresholds> FITNESS_THRESHOLDS = ParameterRequirements.parameters("fitness_thresholds", FitnessThresholds.class);
    public static final ParameterRequirement<TauFitnessThresholds> TAU_FITNESS_THRESHOLDS = ParameterRequirements.parameters("tau_fitness_thresholds", TauFitnessThresholds.class);

    public static <P extends Parameters> ParameterRequirement<P> parameters(String label, Class<P> type) {
        return new ParameterRequirement<>(label, type);
    }

    public static <P extends Parameters> FulfilledDataRequirement<P> parameters(String label, Class<P> type, DataSource<P> dataSource) {
        return parameters(label, type).fulfilWith(dataSource);
    }

    public static <P extends Parameters> FulfilledDataRequirement<P> parameters(ParameterRequirement<P> requirement, DataSource<P> dataSource) {
        return requirement.fulfilWith(dataSource);
    }

}
