package org.processmining.estminer.specpp.supervision.traits;

import org.processmining.estminer.specpp.supervision.monitoring.Monitor;

import java.util.Collection;

public interface Monitoring {

    Collection<Monitor<?, ?>> getMonitors();

}
