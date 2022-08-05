package org.processmining.estminer.specpp.evaluation;

import org.processmining.estminer.specpp.base.CandidateEvaluation;

public enum BasicVariantFitnessStatus implements CandidateEvaluation {
    FITTING, UNDERFED, OVERFED, NOT_ENDING_ON_ZERO
}
