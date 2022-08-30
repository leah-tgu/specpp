package org.processmining.specpp.prom.mvc;

import org.processmining.specpp.prom.util.ColorScheme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.ArrayList;

public class StageProgressionPanel extends JPanel {

    private final SPECppController parentController;
    private SPECppController.PluginStage currentStage;

    private final ArrayList<JButton> stageButtons;

    public StageProgressionPanel(SPECppController parentController) {
        super(new GridBagLayout());
        this.parentController = parentController;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;

        stageButtons = new ArrayList<>();
        for (SPECppController.PluginStage stage : SPECppController.PLUGIN_STAGES) {
            JButton jButton = new JButton(stage.name());
            jButton.setBorder(createUnHighlightedBorder());
            jButton.setMinimumSize(new Dimension(100, 30));
            jButton.addActionListener(e -> {
                if (stage != currentStage) parentController.setPluginStage(stage);
            });
            stageButtons.add(jButton);
            add(jButton, c);
            c.gridx++;
        }

    }

    private Border createHighlightedBorder() {
        return BorderFactory.createSoftBevelBorder(EtchedBorder.RAISED, ColorScheme.lightPink, ColorScheme.lightBlue);
    }

    private Border createUnHighlightedBorder() {
        return BorderFactory.createSoftBevelBorder(EtchedBorder.LOWERED, ColorScheme.lightPink, ColorScheme.lightBlue);
    }

    public void updateCurrentStage(SPECppController.PluginStage stage) {
        SwingUtilities.invokeLater(() -> {
            if (currentStage != null) stageButtons.get(currentStage.ordinal()).setBorder(createUnHighlightedBorder());
            currentStage = stage;
            int ordinal = stage.ordinal();
            stageButtons.get(ordinal).setBorder(createHighlightedBorder());
            for (int i = 0; i < ordinal; i++) {
                stageButtons.get(i).setEnabled(true);
            }
            for (int i = ordinal + 1; i < stageButtons.size(); i++) {
                stageButtons.get(i).setEnabled(false);
            }
        });
    }


}
