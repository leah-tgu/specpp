package org.processmining.specpp.prom.mvc.discovery;

import org.processmining.graphvisualizers.algorithms.GraphVisualizerAlgorithm;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetBuilder;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.util.VizUtils;

import javax.swing.*;
import java.util.List;

public class LivePlacesGraph implements LivePlacesVisualizer {

    private JComponent jComponent;
    private final GraphVisualizerAlgorithm alg;

    public LivePlacesGraph() {
        alg = new GraphVisualizerAlgorithm();
    }


    @Override
    public void update(List<Place> places) {
        ProMPetrinetBuilder pnb = new ProMPetrinetBuilder(places);
        ProMPetrinetWrapper wrapper = pnb.build();
        update(wrapper);
    }

    @Override
    public JComponent getComponent() {
        return jComponent;
    }

    public void update(ProMPetrinetWrapper petrinet) {
        jComponent = alg.apply(null, petrinet.getNet());
        VizUtils.showJComponent("bdf", jComponent, false);
    }
}
