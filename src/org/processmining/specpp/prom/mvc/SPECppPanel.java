package org.processmining.specpp.prom.mvc;

import javax.swing.*;
import java.awt.*;

public class SPECppPanel extends JPanel {

    private final StageProgressionPanel stageProgressionPanel;
    private final SPECppController controller;
    private JPanel mainContentPanel;
    private JPanel mainContent;

    public SPECppPanel(SPECppController controller) {
        super(new GridBagLayout());
        this.controller = controller;

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;

        stageProgressionPanel = new StageProgressionPanel(controller);
        add(stageProgressionPanel, c);

        mainContentPanel = new JPanel(new BorderLayout());
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 1;
        c.weighty = 1;
        add(mainContentPanel, c);

    }

    public void updatePluginStage(SPECppController.PluginStage stage, JPanel panel) {
        stageProgressionPanel.updateCurrentStage(stage);
        SwingUtilities.invokeLater(() -> {
            if (mainContent != null) mainContentPanel.removeAll();
            mainContent = panel;
            mainContentPanel.add(mainContent, BorderLayout.CENTER);
        });
    }
}
