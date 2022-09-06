package org.processmining.specpp.prom.mvc.discovery;

import org.processmining.framework.util.ui.widgets.ProMTableWithoutPanel;
import org.processmining.specpp.prom.alg.LiveEvents;
import org.processmining.specpp.prom.util.Destructible;
import org.processmining.specpp.supervision.monitoring.KeepLastMonitor;
import org.processmining.specpp.supervision.observations.Event;
import org.processmining.specpp.supervision.observations.*;
import org.processmining.specpp.supervision.observations.performance.TaskDescription;
import org.processmining.specpp.util.JavaTypingUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;
import java.util.Optional;

public class EventTable extends JPanel implements Destructible {

    private final DefaultTableModel model;
    private final ProMTableWithoutPanel table;
    private KeepLastMonitor<EventCountStatistics> monitor;

    public EventTable(Optional<LiveEvents> liveEvents) {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"Task Description", "count"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return TaskDescription.class;
                    case 1:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };
        table = new ProMTableWithoutPanel(model);
        add(table, BorderLayout.CENTER);

        if (liveEvents.isPresent()) {
            monitor = liveEvents.get().getMonitor("events", JavaTypingUtils.castClass(KeepLastMonitor.class));
            updateTimer = new Timer(200, e -> updateTable());
            updateTimer.start();
        }
    }

    private Timer updateTimer;

    private void updateTable() {
        Statistics<ClassKey<Event>, Count> copy = monitor.getMonitoringState().copy();
        //TreeSet<Map.Entry<ClassKey<Event>, Count>> entries = new TreeSet<>(Map.Entry.comparingByKey());
        model.setRowCount(0);
        for (Map.Entry<ClassKey<Event>, Count> record : copy.getRecords()) {
            int c = record.getValue().getCount();
            model.addRow(new Object[]{record.getKey(), c});
        }
        model.fireTableDataChanged();
    }

    @Override
    public void destroy() {
        if (updateTimer != null) updateTimer.stop();
    }
}
