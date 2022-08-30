package org.processmining.specpp.prom.mvc.preprocessing;

import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.log.impls.IndexedVariant;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Iterator;

public class VariantPanel extends JPanel {

    private final DefaultTableModel tableModel;

    public VariantPanel() {
        super(new BorderLayout());
        tableModel = new DefaultTableModel(new String[]{"Variant ID", "Frequency", "Variant"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Integer.class;
                    case 1:
                        return Integer.class;
                    case 2:
                        return String.class;
                    default:
                        return super.getColumnClass(columnIndex);
                }
            }
        };
        DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
        TableColumn idColumn = new TableColumn(0, 50, new DefaultTableCellRenderer(), null);
        TableColumn freqColumn = new TableColumn(0, 50, new DefaultTableCellRenderer(), null);
        TableColumn variantColumn = new TableColumn(0, 250, new DefaultTableCellRenderer(), null);
        columnModel.addColumn(idColumn);
        columnModel.addColumn(freqColumn);
        columnModel.addColumn(variantColumn);
        ProMTable proMTable = new ProMTable(tableModel, columnModel);
        proMTable.setAutoCreateRowSorter(true);
        add(proMTable, BorderLayout.CENTER);
    }


    public void updateLog(Log log) {
        SwingUtilities.invokeLater(() -> {
            int oldCount = tableModel.getRowCount();
            tableModel.setRowCount(0);
            tableModel.rowsRemoved(new TableModelEvent(tableModel, 0, oldCount));
            Iterator<IndexedVariant> it = log.iterator();
            while (it.hasNext()) {
                IndexedVariant next = it.next();
                int i = next.getIndex();
                int f = log.getVariantFrequency(i);
                String variantString = next.getVariant().toString();
                tableModel.addRow(new Object[]{i, f, variantString});
            }
            tableModel.newRowsAdded(new TableModelEvent(tableModel, 0, tableModel.getRowCount()));
        });

    }
}
