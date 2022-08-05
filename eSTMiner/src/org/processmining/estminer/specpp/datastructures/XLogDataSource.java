package org.processmining.estminer.specpp.datastructures;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.estminer.specpp.componenting.data.DataSource;
import org.processmining.estminer.specpp.datastructures.encoding.IntEncodings;
import org.processmining.estminer.specpp.datastructures.log.Activity;
import org.processmining.estminer.specpp.datastructures.log.Log;
import org.processmining.estminer.specpp.datastructures.log.Variant;
import org.processmining.estminer.specpp.datastructures.log.impls.*;
import org.processmining.estminer.specpp.datastructures.petri.FinalTransition;
import org.processmining.estminer.specpp.datastructures.petri.InitialTransition;
import org.processmining.estminer.specpp.datastructures.petri.Transition;
import org.processmining.estminer.specpp.datastructures.util.Counter;
import org.processmining.estminer.specpp.datastructures.util.Tuple2;
import org.processmining.estminer.specpp.util.TestFactory;
import org.processmining.plugins.log.util.XESImport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XLogDataSource implements DataSource<InputDataBundle> {

    private final XLog xLog;
    private final boolean introduceStartEndTransitions;

    public XLogDataSource(String logName, boolean introduceStartEndTransitions) {
        this.introduceStartEndTransitions = introduceStartEndTransitions;
        xLog = readLog(TestFactory.LOG_PATH + logName);
    }

    public static InputDataBundle getInputBundle(String logName, boolean introduceStartEndTransitions) {
        if (logName == null) throw new InputLoadingException();
        XLog xLog = readLog(TestFactory.LOG_PATH + logName);
        Tuple2<Log, Map<String, Activity>> tuple = convert(xLog, introduceStartEndTransitions);
        Tuple2<IntEncodings<Transition>, BidiMap<Activity, Transition>> derivedTransitions = deriveTransitions(tuple.getT1(), tuple.getT2());
        return new InputDataBundle(tuple.getT1(), derivedTransitions.getT1(), derivedTransitions.getT2());
    }

    public static Transition makeTransition(Activity activity, String label) {
        if (Factory.ARTIFICIAL_START.equals(activity)) return new InitialTransition(label);
        else if (Factory.ARTIFICIAL_END.equals(activity)) return new FinalTransition(label);
        else return new Transition(label);
    }

    public static Tuple2<IntEncodings<Transition>, BidiMap<Activity, Transition>> deriveTransitions(Log log, Map<String, Activity> activityMapping) {
        BidiMap<Activity, Transition> transitionMapping = new DualHashBidiMap<>();
        activityMapping.forEach((label, activity) -> transitionMapping.put(activity, makeTransition(activity, label)));
        TransitionEncodingsBuilder teb = new AverageTracePositionTransitionEncoder(log, activityMapping, transitionMapping);
        return new Tuple2<>(teb.build(), transitionMapping);
    }

    public static XLog readLog(String path) {
        try {
            return XESImport.readXLog(path);
        } catch (IOException e) {
            throw new InputLoadingException(e);
        }
    }

    public static Tuple2<Log, Map<String, Activity>> convert(XLog input, boolean introduceStartEndTransitions) {
        if (input == null) throw new InputLoadingException();

        XEventClassifier xEventClassifier = new XEventNameClassifier();// input.getClassifiers().get(0);
        Factory factory = new Factory(introduceStartEndTransitions);

        Map<String, Activity> activities = new HashMap<>();
        if (introduceStartEndTransitions) activities.putAll(Factory.getStartEndActivities());

        Counter<Variant> c = new Counter<>();
        for (XTrace trace : input) {
            VariantBuilder<VariantImpl> builder = factory.createVariantBuilder();
            for (XEvent event : trace) {
                String s = xEventClassifier.getClassIdentity(event);
                if (!activities.containsKey(s)) activities.put(s, factory.createActivity(s));
                Activity activity = activities.get(s);
                builder.append(activity);
            }
            VariantImpl v = builder.build();
            c.inc(v);
        }
        LogBuilder<LogImpl> builder = factory.createLogBuilder();
        for (Map.Entry<Variant, Integer> entry : c.entrySet()) {
            builder.appendVariant(entry.getKey(), entry.getValue());
        }
        return new Tuple2<>(builder.build(), activities);
    }

    @Override
    public InputDataBundle getData() {
        Tuple2<Log, Map<String, Activity>> tuple = convert(xLog, introduceStartEndTransitions);
        Tuple2<IntEncodings<Transition>, BidiMap<Activity, Transition>> derivedTransitions = deriveTransitions(tuple.getT1(), tuple.getT2());
        return new InputDataBundle(tuple.getT1(), derivedTransitions.getT1(), derivedTransitions.getT2());
    }

    private static class InputLoadingException extends RuntimeException {
        public InputLoadingException() {
        }

        public InputLoadingException(Throwable cause) {
            super(cause);
        }
    }
}
