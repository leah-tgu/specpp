package org.processmining.specpp.prom.mvc.result;

import org.processmining.specpp.prom.mvc.StageController;

import javax.swing.*;

public class ResultController implements StageController {
    @Override
    public JPanel createPanel() {
        return new ResultPanel();
    }
}
