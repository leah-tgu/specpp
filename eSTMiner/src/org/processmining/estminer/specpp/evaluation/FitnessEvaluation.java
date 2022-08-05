package org.processmining.estminer.specpp.evaluation;

import org.processmining.estminer.specpp.base.CandidateEvaluation;

public class FitnessEvaluation implements CandidateEvaluation {

    private final boolean isFitting;

    public FitnessEvaluation(boolean isFitting) {
        this.isFitting = isFitting;
    }

    public boolean isFitting() {
        return isFitting;
    }

    @Override
    public String toString() {
        return "isFitting=" + isFitting;
    }
}
