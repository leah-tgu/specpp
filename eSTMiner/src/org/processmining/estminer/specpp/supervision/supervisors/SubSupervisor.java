package org.processmining.estminer.specpp.supervision.supervisors;

import org.processmining.estminer.specpp.componenting.delegators.DelegatingObserver;
import org.processmining.estminer.specpp.supervision.observations.LogMessage;

public abstract class SubSupervisor extends SchedulingSupervisor {


    protected final DelegatingObserver<LogMessage> fileLogger = new DelegatingObserver<>();
    protected final DelegatingObserver<LogMessage> consoleLogger = new DelegatingObserver<>();

    public SubSupervisor() {
        componentSystemAdapter().require(BaseSupervisor.FILE_LOGGER_REQUIREMENT, fileLogger)
                                .require(BaseSupervisor.CONSOLE_LOGGER_REQUIREMENT, consoleLogger);
    }


    @Override
    public void init() {
        if (componentSystemAdapter().areAllRequirementsMet()) instantiateObservationHandlingFullySatisfied();
        else instantiateObservationHandlingPartiallySatisfied();
    }

    protected void instantiateObservationHandlingPartiallySatisfied() {

    }

    protected void instantiateObservationHandlingFullySatisfied() {}


}
