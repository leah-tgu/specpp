package org.processmining.specpp.prom.util;

import com.fluxicon.slickerbox.factory.SlickerFactory;

import javax.swing.*;

public class LabeledComboBox<T> extends JPanel {

    private final JComboBox<T> comboBox;

    public LabeledComboBox(String label, T[] values) {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(SlickerFactory.instance().createLabel(label));
        comboBox = SlickerFactory.instance().createComboBox(values);
        add(comboBox);
    }

    public JComboBox<T> getComboBox() {
        return comboBox;
    }
}
