package org.processmining.specpp.prom.mvc.discovery;

import com.google.common.collect.ImmutableMap;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.specpp.composition.events.CandidateAcceptanceRevoked;
import org.processmining.specpp.composition.events.CandidateAccepted;
import org.processmining.specpp.composition.events.CandidateRejected;
import org.processmining.specpp.datastructures.tree.constraints.ClinicallyOverfedPlace;
import org.processmining.specpp.datastructures.tree.constraints.ClinicallyUnderfedPlace;
import org.processmining.specpp.prom.alg.LiveEvents;
import org.processmining.specpp.prom.mvc.swing.SwingFactory;
import org.processmining.specpp.prom.util.Destructible;
import org.processmining.specpp.supervision.monitoring.KeepLastMonitor;
import org.processmining.specpp.supervision.observations.Event;
import org.processmining.specpp.supervision.observations.*;
import org.processmining.specpp.util.JavaTypingUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class EventTable extends JPanel implements Destructible {

    private final DefaultTableModel model;
    private KeepLastMonitor<EventCountStatistics> monitor;
    public static final Map<ClassKey<Event>, String> descriptionDictionary = new ImmutableMap.Builder<ClassKey<Event>, String>().put(new ClassKey<>(ClinicallyUnderfedPlace.class), "A place met the \u25BD(L) >= 1-tau threshold")
                                                                                                                                .put(new ClassKey<>(ClinicallyOverfedPlace.class), "A place met the \u25B3(L) >= 1-tau threshold")
                                                                                                                                .put(new ClassKey<>(CandidateRejected.class), "A place was rejected")
                                                                                                                                .put(new ClassKey<>(CandidateAccepted.class), "A place was accepted")
                                                                                                                                .put(new ClassKey<>(CandidateAcceptanceRevoked.class), "An accepted place was removed again")
                                                                                                                                .build();

    public EventTable(LiveEvents liveEvents) {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"Event Class", "Description", "count"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                    case 1:
                        return ClassKey.class;
                    case 2:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }

        };
        ProMTable table = SwingFactory.proMTable(model);
        table.setAutoCreateRowSorter(true);
        add(table, BorderLayout.CENTER);

        if (liveEvents != null) {
            monitor = liveEvents.getMonitor("events", JavaTypingUtils.castClass(KeepLastMonitor.class));
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
            ClassKey<Event> key = record.getKey();
            model.addRow(new Object[]{key, descriptionDictionary.getOrDefault(key, ""), c});
        }
        model.fireTableDataChanged();
    }

    @Override
    public void destroy() {
        if (updateTimer != null) updateTimer.stop();
    }
}
