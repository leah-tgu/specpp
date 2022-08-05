package org.processmining.estminer.specpp.supervision;

import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.processmining.estminer.specpp.util.FileUtils;

import java.util.HashSet;
import java.util.Set;

public class FileMessageLogger extends MessageLogger {


    public static final String LOG_PATH = "logs\\", DEFAULT_LOGNAME = "main", LOGFILE_SUFFIX = ".log";

    public static final Set<String> instantiatedLoggers = new HashSet<>();

    static {
        FileAppender fileAppender = FileUtils.createLogFileAppender(DEFAULT_LOGNAME);
        LogManager.getLogger("SPECPP File Logger").addAppender(fileAppender);
    }

    public FileMessageLogger() {
        super(LogManager.getLogger("SPECPP File Logger"));
    }

    public static FileMessageLogger create(String loggerLabel) {
        Logger logger = LogManager.getLogger(loggerLabel);
        if (!instantiatedLoggers.contains(loggerLabel)) {
            FileAppender fileAppender = FileUtils.createLogFileAppender(loggerLabel);
            logger.addAppender(fileAppender);
            instantiatedLoggers.add(loggerLabel);
        }
        return new FileMessageLogger(logger);
    }

    public FileMessageLogger(Logger loggerInstance) {
        super(loggerInstance);
    }


}
