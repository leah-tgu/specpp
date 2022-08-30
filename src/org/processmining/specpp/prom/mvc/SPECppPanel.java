package org.processmining.specpp.prom.mvc;

import javax.swing.*;
import java.awt.*;

public class SPECppPanel extends JPanel {

    private final StageProgressionPanel stageProgressionPanel;
    private final SPECppController controller;
    private JPanel currentMainPanel;

    public SPECppPanel(SPECppController controller) {
        super(new GridBagLayout());
        this.controller = controller;

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1;

        stageProgressionPanel = new StageProgressionPanel(controller);
        add(stageProgressionPanel, c);
    }

    public void updatePluginStage(SPECppController.PluginStage stage, JPanel panel) {
        stageProgressionPanel.updateCurrentStage(stage);
        SwingUtilities.invokeLater(() -> {
            if (currentMainPanel != null) remove(currentMainPanel);
            currentMainPanel = panel;
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.gridy = 1;
            c.weightx = 1;
            c.weighty = 1;
            add(panel, c);
        });
    }
}
