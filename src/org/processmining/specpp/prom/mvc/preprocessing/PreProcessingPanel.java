package org.processmining.specpp.prom.mvc.preprocessing;

import org.processmining.specpp.prom.mvc.AbstractStagePanel;

import javax.swing.*;
import java.awt.*;

public class PreProcessingPanel extends AbstractStagePanel<PreProcessingController> {

    public PreProcessingPanel(PreProcessingController controller, JComponent variantPanel, JComponent parametersPanel, JComponent previewPanel) {
        super(controller, new BorderLayout());
        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplit.setDividerLocation(0.5);
        rightSplit.setTopComponent(parametersPanel);
        rightSplit.setBottomComponent(previewPanel);
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(0.5);
        mainSplit.setLeftComponent(variantPanel);
        mainSplit.setRightComponent(rightSplit);
        add(mainSplit, BorderLayout.CENTER);
    }


}
