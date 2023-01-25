package org.processmining.specpp.headless.batch;

import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.supervision.DirectCSVWriter;

import java.util.EnumSet;
import java.util.List;

class BatchContext {

    EnumSet<BatchOptions> options = EnumSet.noneOf(BatchOptions.class);

    List<ProvidesParameters> parameterVariations;
    List<Tuple2<String, List<String>>> informalParameterVariations;
    int num_threads;
    String attempt_identifier, outputFolder, logPath;
    EvalContext evalContext;
    DirectCSVWriter<SPECppFinished> perfWriter;

    public String inOutputFolder(String filename) {
        return outputFolder + filename;
    }

}
