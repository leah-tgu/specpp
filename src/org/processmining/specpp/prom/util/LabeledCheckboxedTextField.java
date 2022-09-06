package org.processmining.specpp.prom.util;

import com.fluxicon.slickerbox.factory.SlickerFactory;

import javax.swing.*;
import java.awt.*;

public class LabeledCheckboxedTextField extends JPanel {

    protected final JTextField field;
    protected final JCheckBox checkBox;

    public LabeledCheckboxedTextField(String label, boolean enabledByDefault) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        checkBox = SlickerFactory.instance().createCheckBox(label, enabledByDefault);
        checkBox.addActionListener(e -> updateTextFieldState());
        add(checkBox);
        JPanel sep = new JPanel();
        sep.setMinimumSize(new Dimension(25, 10));
        add(sep);
        field = new JTextField(30);
        field.setMinimumSize(new Dimension(150, 15));
        //field.setPreferredSize(new Dimension(150, 15));
        add(field);

        updateTextFieldState();
    }

    private void updateTextFieldState() {
        field.setEnabled(checkBox.isEnabled());
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }

    public JTextField getTextField() {
        return field;
    }

    public String getText() {
        return checkBox.isEnabled() ? field.getText() : null;
    }

}
