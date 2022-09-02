package org.processmining.specpp.prom.util;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.framework.util.ui.widgets.ProMComboBox;

import javax.swing.*;

@SuppressWarnings("unchecked")
public class FactoryUtils {

    public static <T> JComboBox<T> comboBox(T[] values) {
        return (JComboBox<T>) SlickerFactory.instance().createComboBox(values);
    }

    public static <T> ProMComboBox<T> promComboBox(T[] values) {
        return new ProMComboBox<>(values);
    }

    public static <T> LabeledComboBox<T> labeledComboBox(String label, T[] values) {
        return new LabeledComboBox<>(label, values);
    }

    public static JCheckBox labeledCheckBox(String label) {
        return labeledCheckBox(label, false);
    }

    public static JCheckBox labeledCheckBox(String label, boolean checked) {
        return SlickerFactory.instance().createCheckBox(label, checked);
    }


    public static JLabel createHeader(String s) {
        return SlickerFactory.instance().createLabel("<html><h3>" + s + "</h3></html>");
    }
}
