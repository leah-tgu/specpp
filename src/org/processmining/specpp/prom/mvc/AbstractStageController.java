package org.processmining.specpp.prom.mvc;

import javax.swing.*;

public abstract class AbstractStageController implements StageController {

    protected final SPECppController parentController;

    public AbstractStageController(SPECppController parentController) {
        this.parentController = parentController;
    }


}
