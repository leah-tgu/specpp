package org.processmining.specpp.prom.mvc.preprocessing;

import org.processmining.framework.util.ui.widgets.ProMSplitPane;

import javax.swing.*;
import java.awt.*;

public class PreProcessingPanel extends JPanel {

    public PreProcessingPanel(JComponent variantPanel, JComponent parametersPanel, JComponent previewPanel) {
        setLayout(new BorderLayout());
        ProMSplitPane rightSplit = new ProMSplitPane(ProMSplitPane.VERTICAL_SPLIT);
        rightSplit.setDividerLocation(0.5);
        rightSplit.setTopComponent(parametersPanel);
        rightSplit.setBottomComponent(previewPanel);
        ProMSplitPane mainSplit = new ProMSplitPane(ProMSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(0.5);
        mainSplit.setLeftComponent(variantPanel);
        mainSplit.setRightComponent(rightSplit);
        add(mainSplit, BorderLayout.CENTER);
    }


}
