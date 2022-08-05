package org.processmining.estminer.specpp.supervision.monitoring;

import org.jfree.chart.JFreeChart;
import org.processmining.estminer.specpp.supervision.observations.Observation;

public interface ChartingMonitor<O extends Observation> extends Monitor<O, JFreeChart> {

}
