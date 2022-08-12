package org.processmining.estminer.specpp.evaluation.fitness;

import org.processmining.estminer.specpp.base.CandidateEvaluation;
import org.processmining.estminer.specpp.datastructures.util.EnumFractions;

public class SimplestFitnessEvaluation extends EnumFractions<SimplifiedFitnessStatus> implements CandidateEvaluation {

    public SimplestFitnessEvaluation(double fittingFraction, double underfedFraction, double overfedFraction, double malfedFraction) {
        super(new double[]{fittingFraction, underfedFraction, overfedFraction, malfedFraction});
    }

}
