package org.processmining.specpp.headless.batch;

import org.processmining.specpp.supervision.CSVWriter;
import org.processmining.specpp.util.EvalUtils;

import java.time.Duration;

class EvalContext {

    Duration timeout;
    EvalUtils.EvaluationLogData evaluationLogData;
    CSVWriter<SPECppEvaluated> evalWriter;

}
