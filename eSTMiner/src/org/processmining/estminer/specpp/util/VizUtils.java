package org.processmining.estminer.specpp.util;

import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.estminer.specpp.supervision.observations.Visualization;
import org.processmining.graphvisualizers.algorithms.GraphVisualizerAlgorithm;
import org.processmining.graphvisualizers.parameters.GraphVisualizerParameters;
import org.processmining.graphvisualizers.plugins.GraphVisualizerPlugin;

import javax.swing.*;

public class VizUtils {
    public static JFrame showVisualization(Visualization<?> vis) {
        JFrame jFrame = new JFrame("Visualization");
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setContentPane(vis.getComponent());
        jFrame.setSize(800, 600);
        jFrame.setVisible(true);
        return jFrame;
    }

    public static JFrame visualizeProMPetrinet(ProMPetrinetWrapper result) {
        GraphVisualizerAlgorithm alg = new GraphVisualizerAlgorithm();
        JComponent apply = alg.apply(null, result.getNet());
        return showJPanel(apply);
    }

    public static JFrame showJPanel(JComponent jComponent) {
        JFrame jFrame = new JFrame("Result");
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jFrame.setAutoRequestFocus(true);
        jFrame.setContentPane(jComponent);
        jFrame.setSize(800, 600);
        jFrame.setVisible(true);
        return jFrame;
    }
}
