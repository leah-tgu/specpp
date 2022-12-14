package org.processmining.specpp.headless.batch;

import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.supervision.CSVWriter;

import java.util.List;

class BatchContext {

    List<ProvidesParameters> parameterVariations;
    int num_threads;
    String attempt_identifier, outputFolder, logPath;
    EvalContext evalContext;
    CSVWriter<SPECppFinished> perfWriter;

    public String inOutputFolder(String filename) {
        return outputFolder + filename;
    }

}
