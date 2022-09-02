package org.processmining.specpp.prom.util;

import com.fluxicon.slickerbox.factory.SlickerFactory;

import javax.swing.*;
import java.awt.*;

public class LabeledTextField extends JPanel {

    private final JTextField field;

    public LabeledTextField(String label, String defaultText) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        add(SlickerFactory.instance().createLabel(label));
        JPanel sep = new JPanel();
        sep.setMinimumSize(new Dimension(25, 10));
        add(sep);
        field = new JTextField();
        field.setText("this is the maximum length");
        field.setMinimumSize(new Dimension(150, 15));
        field.setText(null);
        //field.setPreferredSize(new Dimension(150, 15));
        add(field);
    }

    public JTextField getTextField() {
        return field;
    }
}
