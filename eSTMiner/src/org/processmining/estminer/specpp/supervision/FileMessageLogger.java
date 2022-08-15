package org.processmining.estminer.specpp.supervision;

import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.processmining.estminer.specpp.util.FileUtils;

import java.util.HashSet;
import java.util.Set;

public class FileMessageLogger extends MessageLogger {


    public static final String DEFAULT_LOGNAME = "main", LOGFILE_SUFFIX = ".log";

    public static final Set<String> instantiatedLoggers = new HashSet<>();

    static {
        FileAppender fileAppender = FileUtils.createLogFileAppender(DEFAULT_LOGNAME);
        LogManager.getLogger("SPECPP File Logger").addAppender(fileAppender);
    }

    protected FileMessageLogger() {
        super(LogManager.getLogger("SPECPP File Logger"));
    }

    public static FileMessageLogger create(String loggerLabel, String filePath) {
        Logger logger = LogManager.getLogger(loggerLabel);
        if (!instantiatedLoggers.contains(filePath + loggerLabel)) {
            FileAppender fileAppender = FileUtils.createLogFileAppender(filePath);
            logger.addAppender(fileAppender);
            instantiatedLoggers.add(filePath + loggerLabel);
        }
        return new FileMessageLogger(logger);
    }

    public FileMessageLogger(Logger loggerInstance) {
        super(loggerInstance);
    }


}
