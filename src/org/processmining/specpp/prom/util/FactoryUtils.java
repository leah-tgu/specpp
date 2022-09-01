package org.processmining.specpp.prom.util;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.framework.util.ui.widgets.ProMCheckBoxWithTextField;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.widgets.ProMComboBoxWithTextField;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public class FactoryUtils {

    public static <T> JComboBox<T> comboBox(T[] values) {
        return (JComboBox<T>) SlickerFactory.instance().createComboBox(values);
    }

    public static <T> ProMComboBox<T> promComboBox(T[] values) {
        return new ProMComboBox<>(values);
    }

    public static <T> ProMComboBoxWithTextField labeledPromComboBox(String label, T[] values) {
        return new ProMComboBoxWithTextField(values, label);
    }

    public static ProMCheckBoxWithTextField labeledCheckBox(String label) {
        return labeledCheckBox(label, false);
    }

    public static ProMCheckBoxWithTextField labeledCheckBox(String label, boolean checked) {
        return new ProMCheckBoxWithTextField(checked, label);
    }


}
