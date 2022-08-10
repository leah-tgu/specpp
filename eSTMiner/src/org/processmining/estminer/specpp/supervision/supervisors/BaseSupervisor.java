package org.processmining.estminer.specpp.supervision.supervisors;

import org.processmining.estminer.specpp.componenting.supervision.ObserverRequirement;
import org.processmining.estminer.specpp.supervision.AbstractSupervisor;
import org.processmining.estminer.specpp.supervision.observations.LogMessage;
import org.processmining.estminer.specpp.supervision.observations.performance.PerformanceEvent;
import org.processmining.estminer.specpp.supervision.piping.ConcurrencyBridge;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;

import static org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements.observer;

public class BaseSupervisor extends AbstractSupervisor {

    public static final ObserverRequirement<LogMessage> FILE_LOGGER_REQUIREMENT = observer("logger.file", LogMessage.class);
    public static final ObserverRequirement<LogMessage> CONSOLE_LOGGER_REQUIREMENT = observer("logger.console", LogMessage.class);

    private final ConcurrencyBridge<PerformanceEvent> performanceEventConcurrencyBridge = PipeWorks.concurrencyBridge();

    public BaseSupervisor() {
        componentSystemAdapter().provide(observer(FILE_LOGGER_REQUIREMENT, PipeWorks.fileLogger()))
                                .provide(observer(CONSOLE_LOGGER_REQUIREMENT, PipeWorks.consoleLogger()));

    }

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
