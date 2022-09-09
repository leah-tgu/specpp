package org.processmining.specpp.prom.mvc.swing;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.widgets.ProMTable;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.event.MouseEvent;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class SwingFactory {

    public static <T> JComboBox<T> comboBox(T[] values) {
        return (JComboBox<T>) SlickerFactory.instance().createComboBox(values);
    }

    public static <T> ProMComboBox<T> promComboBox(T[] values) {
        return new ProMComboBox<>(values);
    }

    public static <T> LabeledComboBox<T> labeledComboBox(String label, T[] values) {
        return new LabeledComboBox<>(label, values);
    }

    public static LabeledTextField labeledTextField(String label, int inputTextColumns) {
        return new LabeledTextField(label, inputTextColumns);
    }

    public static <T> TextBasedInputField<T> textBasedInputField(String label, Function<String, T> parseInput, int inputTextColumns) {
        return new TextBasedInputField<>(label, parseInput, inputTextColumns);
    }

    public static <T> ActivatableTextBasedInputField<T> activatableTextBasedInputField(String label, boolean activatedByDefault, Function<String, T> parseInput, int textInputColumns) {
        return new ActivatableTextBasedInputField<>(label, parseInput, activatedByDefault, textInputColumns);
    }

    public static LabeledCheckboxedTextField labeledCheckboxedTextField(String label, boolean enabledByDefault, int inputTextColumns) {
        return new LabeledCheckboxedTextField(label, enabledByDefault, inputTextColumns);
    }

    public static JCheckBox labeledCheckBox(String label) {
        return labeledCheckBox(label, false);
    }

    public static JCheckBox labeledCheckBox(String label, boolean checked) {
        return SlickerFactory.instance().createCheckBox(label, checked);
    }

    public static ProMTable proMTable(TableModel tableModel) {
        return new ProMTable(tableModel) {
            @Override
            public String getToolTipText(MouseEvent event) {
                String tip = null;
                java.awt.Point p = event.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                if (rowIndex < 0 || colIndex < 0) return null;

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException ignored) {
                }

                return tip;
            }
        };
    }

    public static JLabel createHeader(String s) {
        return SlickerFactory.instance().createLabel("<html><h3>" + s + "</h3></html>");
    }

    public static JLabel createHeaderWithSubtitle(String s, String sub) {
        return SlickerFactory.instance().createLabel("<html><h3>" + s + "</h3><br>" + sub + "</html>");
    }

}
