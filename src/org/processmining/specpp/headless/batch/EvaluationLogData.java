package org.processmining.specpp.headless.batch;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;

import java.util.Set;

public class EvaluationLogData {

    private XLog evalLog;
    private XEventClassifier eventClassifier;
    private Set<XEventClass> eventClasses;

    public EvaluationLogData(XLog evalLog, XEventClassifier eventClassifier, Set<XEventClass> eventClasses) {
        this.evalLog = evalLog;
        this.eventClassifier = eventClassifier;
        this.eventClasses = eventClasses;
    }

    public Set<XEventClass> getEventClasses() {
        return eventClasses;
    }

    public void setEventClasses(Set<XEventClass> eventClasses) {
        this.eventClasses = eventClasses;
    }

    public XLog getEvalLog() {
        return evalLog;
    }

    public void setEvalLog(XLog evalLog) {
        this.evalLog = evalLog;
    }

    public XEventClassifier getEventClassifier() {
        return eventClassifier;
    }

    public void setEventClassifier(XEventClassifier eventClassifier) {
        this.eventClassifier = eventClassifier;
    }
}
