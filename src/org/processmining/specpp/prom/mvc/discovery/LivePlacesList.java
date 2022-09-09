package org.processmining.specpp.prom.mvc.discovery;

import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.specpp.datastructures.petri.Place;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class LivePlacesList implements LivePlacesVisualizer {

    private final DefaultTableModel tableModel;
    private final ProMTable table;

    public LivePlacesList() {
        tableModel = new DefaultTableModel(new String[]{"Preset", "Postset"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new ProMTable(tableModel);
    }

    @Override
    public void update(List<Place> places) {
        tableModel.setRowCount(0);
        for (Place place : places) {
            tableModel.addRow(new Object[]{place.preset(), place.postset()});
        }
        tableModel.fireTableDataChanged();
    }

    @Override
    public JComponent getComponent() {
        return table;
    }


}
