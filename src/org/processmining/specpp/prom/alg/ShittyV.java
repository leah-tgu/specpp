package org.processmining.specpp.prom.alg;

import org.processmining.graphvisualizers.algorithms.GraphVisualizerAlgorithm;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.mvc.error.MessagePanel;

import javax.swing.*;

public class ShittyV {

    public static JComponent iz(ProMPetrinetWrapper proMPetrinet) {
        if (proMPetrinet.getNodes().size() > 150) return new MessagePanel("Graph is too large to visualize.");
        GraphVisualizerAlgorithm alg = new GraphVisualizerAlgorithm();
        return alg.apply(null, proMPetrinet);
    }

}
