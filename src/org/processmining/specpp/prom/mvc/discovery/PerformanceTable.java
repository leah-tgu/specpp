package org.processmining.specpp.prom.mvc.discovery;

import org.processmining.framework.util.ui.widgets.ProMTableWithoutPanel;
import org.processmining.specpp.prom.alg.LivePerformance;
import org.processmining.specpp.prom.util.Destructible;
import org.processmining.specpp.supervision.monitoring.PerformanceStatisticsMonitor;
import org.processmining.specpp.supervision.observations.Statistics;
import org.processmining.specpp.supervision.observations.performance.PerformanceStatistic;
import org.processmining.specpp.supervision.observations.performance.TaskDescription;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

public class PerformanceTable extends JPanel implements Destructible {

    private final DefaultTableModel model;
    private final ProMTableWithoutPanel table;
    private PerformanceStatisticsMonitor monitor;

    public PerformanceTable(Optional<LivePerformance> livePerformance) {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"Task Description", "avg", "min", "max", "sum", "count", "it/s"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return TaskDescription.class;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        return Duration.class;
                    case 5:
                    case 6:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };
        table = new ProMTableWithoutPanel(model);
        add(table, BorderLayout.CENTER);

        if (livePerformance.isPresent()) {
            monitor = livePerformance.get().getMonitor("performance", PerformanceStatisticsMonitor.class);
            updateTimer = new Timer(200, e -> updateTable());
            updateTimer.start();
        }
    }

    private Timer updateTimer;

    private void updateTable() {
        Statistics<TaskDescription, PerformanceStatistic> copy = monitor.getMonitoringState().copy();
        model.setRowCount(0);
        for (Map.Entry<TaskDescription, PerformanceStatistic> record : copy.getRecords()) {
            PerformanceStatistic performanceStatistic = record.getValue();
            model.addRow(new Object[]{record.getKey(), performanceStatistic.avg(), performanceStatistic.min(), performanceStatistic.max(), performanceStatistic.sum(), performanceStatistic.N(), performanceStatistic.rate()});
        }
        model.fireTableDataChanged();
    }

    @Override
    public void destroy() {
        if (updateTimer != null) updateTimer.stop();
    }
}
