package org.processmining.specpp.orchestra;

import org.deckfour.xes.model.XLog;
import org.processmining.specpp.componenting.data.DataSource;
import org.processmining.specpp.datastructures.log.ParsedLog;
import org.processmining.specpp.preprocessing.XLogParser;

public interface PreProcessingStrategy {

    ParsedLog parse(XLog xLog, PreProcessingParameters parameters);

    default DataSource<ParsedLog> getParser(XLog xLog, PreProcessingParameters parameters) {
        return () -> parse(xLog, parameters);
    }

}
