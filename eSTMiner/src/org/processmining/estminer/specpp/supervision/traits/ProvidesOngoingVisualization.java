package org.processmining.estminer.specpp.supervision.traits;

import org.jfree.chart.ChartPanel;
import org.processmining.estminer.specpp.supervision.observations.Visualization;

import javax.swing.*;

public interface ProvidesOngoingVisualization<V extends JComponent> {

    Visualization<V> getOngoingVisualization();

}
