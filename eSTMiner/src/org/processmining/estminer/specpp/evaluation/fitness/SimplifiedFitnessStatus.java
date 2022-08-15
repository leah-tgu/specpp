package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.base.CandidateEvaluation;

public enum SimplifiedFitnessStatus implements CandidateEvaluation {
    FITTING, UNDERFED, OVERFED, MALFED
}
