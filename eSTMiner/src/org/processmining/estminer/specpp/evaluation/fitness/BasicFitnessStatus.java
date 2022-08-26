package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.base.CandidateEvaluation;

public enum BasicFitnessStatus implements CandidateEvaluation {
    FITTING, UNDERFED, OVERFED, ACTIVATED, UNACTIVATED
}
