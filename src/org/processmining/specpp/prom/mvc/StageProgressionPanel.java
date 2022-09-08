package org.processmining.specpp.prom.mvc;

import org.processmining.specpp.prom.mvc.swing.ColorScheme;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;

public class StageProgressionPanel extends JPanel {

    private final SPECppController parentController;
    private SPECppController.PluginStage currentStage;

    private final ArrayList<JButton> stageButtons;

    public StageProgressionPanel(SPECppController parentController) {
        super(new GridBagLayout());
        this.parentController = parentController;

        setBorder(BorderFactory.createRaisedBevelBorder());

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.anchor = GridBagConstraints.NORTH;
        c.fill = GridBagConstraints.NONE;

        stageButtons = new ArrayList<>();
        for (SPECppController.PluginStage stage : SPECppController.PLUGIN_STAGES) {
            JButton jButton = new JButton(stage.name());//new SlickerButton(stage.name());
            jButton.setOpaque(false);
            jButton.setBorder(createUnHighlightedBorder());
            jButton.setMinimumSize(new Dimension(150, 50));
            jButton.setPreferredSize(new Dimension(150, 50));
            jButton.addActionListener(e -> {
                if (stage != currentStage) parentController.setPluginStage(stage);
            });
            stageButtons.add(jButton);
            c.gridy = 0;
            add(jButton, c);
        }

    }

    private Border createHighlightedBorder() {
        return BorderFactory.createLineBorder(ColorScheme.lightPink, 5, true);
    }

    private Border createUnHighlightedBorder() {
        return BorderFactory.createLineBorder(ColorScheme.lightBlue, 5, true);
    }

    public void updateCurrentStage(SPECppController.PluginStage stage) {
        SwingUtilities.invokeLater(() -> {
            if (currentStage != null) stageButtons.get(currentStage.ordinal()).setBorder(createUnHighlightedBorder());
            currentStage = stage;
            int ordinal = stage.ordinal();
            stageButtons.get(ordinal).setBorder(createHighlightedBorder());
            for (int i = 0; i <= ordinal; i++) {
                stageButtons.get(i).setEnabled(true);
            }
            for (int i = ordinal + 1; i < stageButtons.size(); i++) {
                stageButtons.get(i).setEnabled(false);
            }
        });
    }

    public void unlockStageButton(SPECppController.PluginStage stage) {
        SwingUtilities.invokeLater(() -> stageButtons.get(stage.ordinal()).setEnabled(true));
    }

}
