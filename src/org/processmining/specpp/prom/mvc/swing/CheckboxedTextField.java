package org.processmining.specpp.prom.mvc.swing;

import com.fluxicon.slickerbox.factory.SlickerFactory;

import javax.swing.*;

public class CheckboxedTextField extends JPanel {

    protected final JTextField field;
    protected final JCheckBox checkBox;

    public CheckboxedTextField(String label, boolean enabledByDefault, int inputTextColumns) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        checkBox = SlickerFactory.instance().createCheckBox(label, enabledByDefault);
        checkBox.addActionListener(e -> updateTextFieldState());
        add(checkBox);
        add(Box.createHorizontalStrut(15));
        field = new JTextField(inputTextColumns);

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
