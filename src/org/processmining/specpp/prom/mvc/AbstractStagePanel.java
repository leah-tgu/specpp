package org.processmining.specpp.prom.mvc;

import org.processmining.specpp.prom.mvc.config.ConfigurationController;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractStagePanel<C extends StageController>  extends JPanel {

    protected final C controller;

    public AbstractStagePanel(C controller) {
        this.controller = controller;
    }

    public AbstractStagePanel(C controller, LayoutManager layout) {
        super(layout);
        this.controller = controller;
    }
}
