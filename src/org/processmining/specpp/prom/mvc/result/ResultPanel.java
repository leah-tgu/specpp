package org.processmining.specpp.prom.mvc.result;

import org.processmining.specpp.prom.mvc.AbstractStagePanel;

import javax.swing.*;
import java.awt.*;

public class ResultPanel extends AbstractStagePanel<ResultController> {
    public ResultPanel(ResultController controller) {
        super(controller);
    }

    public ResultPanel(ResultController controller, LayoutManager layout) {
        super(controller, layout);
    }
}
