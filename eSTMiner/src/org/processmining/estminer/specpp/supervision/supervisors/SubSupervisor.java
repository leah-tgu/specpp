package org.processmining.estminer.specpp.supervision.supervisors;

import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingObserver;
import org.processmining.estminer.specpp.config.parameters.OutputPathParameters;
import org.processmining.estminer.specpp.supervision.observations.LogMessage;

public abstract class SubSupervisor extends SchedulingSupervisor {


    protected final DelegatingObserver<LogMessage> fileLogger = new DelegatingObserver<>();
    protected final DelegatingObserver<LogMessage> consoleLogger = new DelegatingObserver<>();

    protected final DelegatingDataSource<OutputPathParameters> pathParametersSource = new DelegatingDataSource<>();

    public SubSupervisor() {
        globalComponentSystem().require(BaseSupervisor.FILE_LOGGER_REQUIREMENT, fileLogger)
                               .require(BaseSupervisor.CONSOLE_LOGGER_REQUIREMENT, consoleLogger)
                               .require(ParameterRequirements.OUTPUT_PATH_PARAMETERS, pathParametersSource);
    }


    @Override
    public void initSelf() {
        if (globalComponentSystem().areAllRequirementsMet()) instantiateObservationHandlingFullySatisfied();
        else instantiateObservationHandlingPartiallySatisfied();
    }

    protected void instantiateObservationHandlingPartiallySatisfied() {

    }

    protected void instantiateObservationHandlingFullySatisfied() {
        instantiateObservationHandlingPartiallySatisfied();
    }


}
