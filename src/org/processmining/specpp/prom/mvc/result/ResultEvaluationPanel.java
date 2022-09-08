package org.processmining.specpp.prom.mvc.result;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;

import javax.swing.*;
import java.awt.*;

public class ResultEvaluationPanel extends JPanel {
    public ResultEvaluationPanel(ProMPetrinetWrapper proMPetrinetWrapper, PetriNet petriNet) {
        super(new GridBagLayout());

        add(SlickerFactory.instance()
                          .createLabel(String.format("Petri net contains %d transitions, %d places & %d arcs.", proMPetrinetWrapper.getTransitions()
                                                                                                                                  .size(), proMPetrinetWrapper.getPlaces()
                                                                                                                                                              .size(), proMPetrinetWrapper.getEdges()
                                                                                                                                                                                          .size())));

    }
}
