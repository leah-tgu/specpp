package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.base.CandidateEvaluation;

public enum BasicVariantFitnessStatus implements CandidateEvaluation {
    FITTING, GOES_NEGATIVE, NON_SAFE, NOT_ENDING_ON_ZERO
}
