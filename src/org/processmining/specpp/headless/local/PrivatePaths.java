package org.processmining.specpp.headless.local;


import org.processmining.specpp.util.PathTools;

public class PrivatePaths {

    public static final String LOG_PATH = String.join(PathTools.PATH_FOLDER_SEPARATOR, "C:", "Users", "Leah", "Documents", "Event Logs") + PathTools.PATH_FOLDER_SEPARATOR;

    public static final String BPI11 = String.join(PathTools.PATH_FOLDER_SEPARATOR, "BPIC 2011", "Hospital_log.xes");
    public static final String BPI12 = String.join(PathTools.PATH_FOLDER_SEPARATOR, "BPIC 2012", "BPI_Challenge_2012.xes.gz");
    public static final String BPI18 = String.join(PathTools.PATH_FOLDER_SEPARATOR, "BPIC 2018", "BPI Challenge 2018.xes.gz");
    public static final String WILWILLES_REDUCED_NO_PARALELLISM = String.join(PathTools.PATH_FOLDER_SEPARATOR, "Synthetic", "Lisa", "wilwilles-reduced(noParalellism).xes");
    public static final String ROAD_TRAFFIC_FINE_MANAGEMENT_PROCESS = String.join(PathTools.PATH_FOLDER_SEPARATOR, "Synthetic", "Road_Traffic_Fine_Management_Process.xes");
    public static final String BPIC12_A_projection = String.join(PathTools.PATH_FOLDER_SEPARATOR, "BPIC 2012", "A_projection.xes");
    public static final String BPIC12_O_projection = String.join(PathTools.PATH_FOLDER_SEPARATOR, "BPIC 2012", "O_projection.xes");
    public static final String BPIC12_W_projection = String.join(PathTools.PATH_FOLDER_SEPARATOR, "BPIC 2012", "W_projection.xes");

    public static String toAbsolutePath(String logName) {
        return LOG_PATH + logName;
    }

}
