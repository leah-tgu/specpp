package org.processmining.specpp.prom.mvc.discovery;

import org.processmining.specpp.prom.mvc.StageController;

import javax.swing.*;

public class DiscoveryController implements StageController {
    @Override
    public JPanel createPanel() {
        return new DiscoveryPanel();
    }
}
