package org.processmining.estminer.specpp.supervision.monitoring;

import org.processmining.estminer.specpp.supervision.observations.Observation;
import org.processmining.estminer.specpp.traits.PrettyPrintable;

public class KeepLastMonitor<O extends Observation> implements ComputingMonitor<O, O, String> {

    private O last;

    @Override
    public String computeResult() {
        return last instanceof PrettyPrintable ? ((PrettyPrintable) last).toPrettyString() : last.toString();
    }

    @Override
    public O getMonitoringState() {
        return last;
    }

    @Override
    public void handleObservation(O observation) {
        last = observation;
    }
}
