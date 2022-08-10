package org.processmining.estminer.specpp.supervision.supervisors;

import org.processmining.estminer.specpp.supervision.MessageLogger;
import org.processmining.estminer.specpp.supervision.observations.DebugEvent;
import org.processmining.estminer.specpp.supervision.observations.LogMessage;
import org.processmining.estminer.specpp.supervision.piping.Observer;
import org.processmining.estminer.specpp.supervision.piping.PipeWorks;
import org.processmining.estminer.specpp.supervision.transformers.Transformers;

import java.util.Objects;

public class DebuggingSupervisor extends SubSupervisor {


    private static MessageLogger staticDebuggingLogger;

    public DebuggingSupervisor() {

    }

    @Override
    protected void instantiateObservationHandlingFullySatisfied() {

    }

    public static void debug(DebugEvent e) {
        getDebugLogger().observe(Transformers.toLogMessage().apply(e));
    }

    private static Observer<LogMessage> getDebugLogger() {
        if (staticDebuggingLogger == null) staticDebuggingLogger = PipeWorks.consoleLogger();
        return staticDebuggingLogger;
    }

    public static void debug(Object o) {
        debug(new DebugEvent(Objects.toString(o)));
    }

    public static void debug(String desc, Object o) {
        debug(new DebugEvent(desc + ": " + o));
    }


}
