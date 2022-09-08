package org.processmining.specpp.prom.mvc.result;

import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.mvc.AbstractStageController;
import org.processmining.specpp.prom.mvc.SPECppController;

import javax.swing.*;

public class ResultController extends AbstractStageController {
    public ResultController(SPECppController parentController) {
        super(parentController);
    }



    @Override
    public JPanel createPanel() {
        return new ResultPanel(this, parentController.getResult(), parentController.getIntermediatePostProcessingResults());
    }

    public ProMPetrinetWrapper getPetrinet() {
        return parentController.getResult();
    }
}
