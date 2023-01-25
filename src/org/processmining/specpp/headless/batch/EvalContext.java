package org.processmining.specpp.headless.batch;

import org.processmining.specpp.supervision.CSVWriter;
import org.processmining.specpp.supervision.DirectCSVWriter;
import org.processmining.specpp.supervision.piping.Observer;
import org.processmining.specpp.util.EvalUtils;

import java.time.Duration;

class EvalContext {

    Duration timeout;
    EvalUtils.EvaluationLogData evaluationLogData;
    DirectCSVWriter<SPECppEvaluated> evalWriter;

}
