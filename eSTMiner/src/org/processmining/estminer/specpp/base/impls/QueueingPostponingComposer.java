package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.Composer;
import org.processmining.estminer.specpp.base.Composition;
import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingEvaluator;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluationRequirements;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.evaluation.fitness.SimplestFitnessEvaluation;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

import java.util.LinkedList;
import java.util.List;

public abstract class QueueingPostponingComposer<C extends Candidate, I extends Composition<C>, R extends Result, L extends CandidateConstraint<C>> extends AbstractPostponingComposer<C, I, R, L> {

    private final List<C> postponedCandidates;
    private final DelegatingEvaluator<Place, SimplestFitnessEvaluation> fitnessEvaluator = new DelegatingEvaluator<>();

    public QueueingPostponingComposer(Composer<C, I, R> childComposer) {
        super(childComposer);
        postponedCandidates = new LinkedList<>();
        componentSystemAdapter().require(EvaluationRequirements.SIMPLE_FITNESS, fitnessEvaluator)
                                .provide(SupervisionRequirements.observable("postponing_composer.constraints", JavaTypingUtils.castClass(CandidateConstraint.class), getConstraintPublisher()));
    }

    @Override
    protected boolean deliberateImmediateAcceptance(C candidate) {
        return false;
    }

    @Override
    protected boolean deliberateImmediateRejection(C candidate) {

        return false;
    }

    @Override
    protected void postponeDecision(C candidate) {
        postponedCandidates.add(candidate);
    }

    @Override
    protected void handlePostponedDecisions() {
        for (C postponedCandidate : postponedCandidates) {
            // TODO CONTINUE HERE
        }
    }

    @Override
    protected void rejectCandidate(C candidate) {

    }

}
