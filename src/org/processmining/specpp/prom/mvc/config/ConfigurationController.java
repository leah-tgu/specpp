package org.processmining.specpp.prom.mvc.config;

import org.processmining.specpp.prom.mvc.AbstractStageController;
import org.processmining.specpp.prom.mvc.SPECppController;
import org.processmining.specpp.prom.mvc.StageController;

import javax.swing.*;

public class ConfigurationController extends AbstractStageController {


    public ConfigurationController(SPECppController parentController) {
        super(parentController);
    }

    @Override
    public JPanel createPanel() {
        return new ConfigurationPanel(this);
    }

    public void basicConfigCompleted(Object o) {
        parentController.configCompleted(null);
    }

}
