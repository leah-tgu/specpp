package org.processmining.estminer.specpp.supervision.monitoring;

import org.processmining.estminer.specpp.supervision.observations.Observation;

public class KeepLastMonitor<O extends Observation> implements Monitor<O, O> {

    private O last;

    @Override
    public O getMonitoringState() {
        return last;
    }

    @Override
    public void handleObservation(O observation) {
        last = observation;
    }

}
