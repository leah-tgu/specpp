package org.processmining.estminer.specpp.supervision.supervisors;

import org.processmining.estminer.specpp.componenting.data.ParameterRequirements;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.estminer.specpp.componenting.supervision.ObserverRequirement;
import org.processmining.estminer.specpp.config.parameters.OutputPathParameters;
import org.processmining.estminer.specpp.config.parameters.SupervisionParameters;
import org.processmining.estminer.specpp.supervision.AbstractSupervisor;
import org.processmining.estminer.specpp.supervision.MessageLogger;
import org.processmining.estminer.specpp.supervision.observations.LogMessage;
import org.processmining.estminer.specpp.supervision.piping.Observer;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.util.PathTools;

import static org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements.observer;

public class BaseSupervisor extends AbstractSupervisor {

    public static final ObserverRequirement<LogMessage> FILE_LOGGER_REQUIREMENT = observer("logger.file", LogMessage.class);
    public static final ObserverRequirement<LogMessage> CONSOLE_LOGGER_REQUIREMENT = observer("logger.console", LogMessage.class);

    private final DelegatingDataSource<OutputPathParameters> outputPathParameters = new DelegatingDataSource<>();
    private final DelegatingDataSource<SupervisionParameters> supervisionParameters = new DelegatingDataSource<>();

    public BaseSupervisor() {
        componentSystemAdapter().require(ParameterRequirements.OUTPUT_PATH_PARAMETERS, outputPathParameters)
                                .require(ParameterRequirements.SUPERVISION_PARAMETERS, supervisionParameters);
    }


    @Override
    public void initSelf() {
        if (supervisionParameters.isSet()) {
            Observer<LogMessage> observer = o -> {
            };
            if (supervisionParameters.getData().isUseConsole()) observer = PipeWorks.consoleLogger();
            componentSystemAdapter().provide(observer(CONSOLE_LOGGER_REQUIREMENT, observer));
        }
        if (outputPathParameters.isSet()) {
            OutputPathParameters parameters = outputPathParameters.getData();
            String filePath = parameters.getFilePath(PathTools.OutputFileType.MAIN_LOG, "main");
            MessageLogger fileLogger = PipeWorks.fileLogger("main", filePath);
            componentSystemAdapter().provide(observer(FILE_LOGGER_REQUIREMENT, fileLogger));
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
