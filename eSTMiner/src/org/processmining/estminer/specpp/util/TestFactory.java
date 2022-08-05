package org.processmining.estminer.specpp.util;

import org.processmining.estminer.specpp.representations.InputDataBundle;
import org.processmining.estminer.specpp.representations.XLogDataSource;
import org.processmining.estminer.specpp.representations.petri.ProMPetrinetWrapper;
import org.processmining.estminer.specpp.supervision.observations.Visualization;
import org.processmining.graphvisualizers.algorithms.GraphVisualizerAlgorithm;

import javax.swing.*;

public class TestFactory {

    public static final String LOG_PATH = "C:\\Users\\Leah\\Documents\\Event Logs\\Synthetic\\";
    public static final String LOG_1 = "ETM_Configuration1.xes.gz";
    public static final String LOG_2 = "Lisa\\Trace_AEDF.xes";
    public static final String LOG_3 = "Lisa\\CRBeispiel.xes";
    public static final String LOG_4 = "Lisa\\wilwilles-reduced(noParalellism).xes";
    public static final String LOG_5 = "Road_Traffic_Fine_Management_Process.xes.gz";

    public static InputDataBundle defaultInputBundle() {
        return XLogDataSource.getInputBundle(LOG_4, true);
    }


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
