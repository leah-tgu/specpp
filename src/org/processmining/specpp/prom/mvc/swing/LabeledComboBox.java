package org.processmining.specpp.prom.mvc.swing;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.specpp.prom.mvc.config.ProMConfig;

import javax.swing.*;
import java.awt.*;

public class LabeledComboBox<T> extends HorizontalJPanel {

    private final JComboBox<T> comboBox;

    public LabeledComboBox(String label, T[] values) {
        add(SlickerFactory.instance().createLabel(label));
        comboBox = SlickerFactory.instance().createComboBox(values);
        comboBox.setRenderer(SwingFactory.getMyListCellRenderer());

        add(comboBox);
    }

    public JComboBox<T> getComboBox() {
        return comboBox;
    }
}
