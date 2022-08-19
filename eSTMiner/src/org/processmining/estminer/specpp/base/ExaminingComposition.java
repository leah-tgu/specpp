package org.processmining.estminer.specpp.base;

import org.processmining.estminer.specpp.componenting.evaluation.FulfilledEvaluatorRequirement;

public interface ExaminingComposition<C extends Candidate, E extends CandidateEvaluation> extends Composition<C> {

    Evaluator<C, E> getExaminationFunction();

    FulfilledEvaluatorRequirement<C, E> getExaminingEvaluator();

}
