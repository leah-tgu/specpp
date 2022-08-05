package org.processmining.estminer.specpp.supervision.monitoring;

import org.processmining.estminer.specpp.supervision.observations.Observation;

public interface ComputingMonitor<O extends Observation, R, F> extends Monitor<O, R> {

    F computeResult();

}
