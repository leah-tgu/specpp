package org.processmining.specpp.headless.batch;

import org.processmining.specpp.supervision.CSVWriter;

import java.time.Duration;

class EvalContext {

    Duration timeout;
    EvaluationLogData evaluationLogData;
    CSVWriter<SPECppEvaluated> evalWriter;

}
