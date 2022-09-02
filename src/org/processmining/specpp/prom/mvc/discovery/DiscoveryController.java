package org.processmining.specpp.prom.mvc.discovery;

import org.processmining.specpp.prom.mvc.AbstractStageController;
import org.processmining.specpp.prom.mvc.SPECppController;
import org.processmining.specpp.prom.mvc.StageController;

import javax.swing.*;

public class DiscoveryController extends AbstractStageController {
    public DiscoveryController(SPECppController parentController) {
        super(parentController);
    }

    @Override
    public JPanel createPanel() {
        return new DiscoveryPanel(this);
    }
}
