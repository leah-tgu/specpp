package org.processmining.specpp.prom.mvc.swing;

import com.fluxicon.slickerbox.factory.SlickerFactory;

import javax.swing.*;
import java.awt.*;

public class LabeledTextField extends JPanel {

    protected final JTextField field;

    public LabeledTextField(String label, int inputTextColumns) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        add(SlickerFactory.instance().createLabel(label));
        add(Box.createHorizontalStrut(20));
        field = new JTextField(inputTextColumns);
        add(field);
    }

    public JTextField getTextField() {
        return field;
    }

    public String getText() {
        return field.getText();
    }

}
