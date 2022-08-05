package org.processmining.estminer.specpp.util;

import org.apache.log4j.FileAppender;
import org.apache.log4j.SimpleLayout;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.processmining.estminer.specpp.supervision.FileMessageLogger;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.processmining.plugins.graphviz.visualisation.export.ExporterSVG;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

    public static final String OUTPUT_PATH = "output\\";
    public static final String CHART_PATH = OUTPUT_PATH + "charts\\";
    public static final String LOG_PATH = OUTPUT_PATH + "logs\\";


    public static void saveDot(Dot dot) {
        ExporterSVG exporter = new ExporterSVG();
        File f = new File(OUTPUT_PATH + "graph_" + dot.getLabel() + ".svg");
        try {
            exporter.export(new DotPanel(dot), f);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveChart(JFreeChart chart) {
        File f = new File(CHART_PATH + chart.getTitle().getText().replace(" ", "").replace(".", "_") + ".png");
        try {
            ChartUtilities.saveChartAsPNG(f, chart, 1920, 1080);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static FileAppender createLogFileAppender(String loggerLabel) {
        FileAppender fileAppender = null;
        try {
            fileAppender = new FileAppender(new SimpleLayout(), LOG_PATH + loggerLabel + FileMessageLogger.LOGFILE_SUFFIX, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileAppender;
    }

    public static FileWriter createOutputFileWriter(String fileName) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(OUTPUT_PATH + fileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileWriter;
    }
}
