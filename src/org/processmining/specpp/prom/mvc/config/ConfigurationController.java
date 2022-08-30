package org.processmining.specpp.prom.mvc.config;

import org.processmining.specpp.prom.mvc.StageController;

import javax.swing.*;

public class ConfigurationController implements StageController {
    @Override
    public JPanel createPanel() {
        return new ConfigurationPanel();
    }
}
