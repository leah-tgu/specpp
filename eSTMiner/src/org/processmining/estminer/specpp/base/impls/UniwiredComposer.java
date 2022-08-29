package org.processmining.estminer.specpp.base.impls;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.componenting.delegators.DelegatingObserver;
import org.processmining.estminer.specpp.componenting.supervision.SupervisionRequirements;
import org.processmining.estminer.specpp.componenting.system.link.ComposerComponent;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.proposal.ProposerSignal;
import org.processmining.estminer.specpp.proposal.RestartProposer;
import org.processmining.estminer.specpp.supervision.supervisors.DebuggingSupervisor;
import org.processmining.estminer.specpp.util.JavaTypingUtils;

import java.util.ArrayList;
import java.util.Random;

public class UniwiredComposer<I extends AdvancedComposition<Place>, R extends Result> extends AbstractPostponingComposer<Place, I, R, CandidateConstraint<Place>> {

    protected final ArrayList<Place> collectedPlaces;
    protected final DelegatingObserver<ProposerSignal> proposerSignalsIn = new DelegatingObserver<>();

    protected int targetTreeLevel = 0;

    public UniwiredComposer(ComposerComponent<Place, I, R> childComposer) {
        super(childComposer);
        collectedPlaces = new ArrayList<>();

        localComponentSystem().require(SupervisionRequirements.observer("proposer.signals.in", ProposerSignal.class), proposerSignalsIn);
    }

    @Override
    protected CandidateDecision deliberateCandidate(Place candidate) {
        if (candidate.size() >= targetTreeLevel && !collectedPlaces.isEmpty()) {
            targetTreeLevel = candidate.size();
            DebuggingSupervisor.debug("uniwired", "testing restart triggered by" + candidate);
            handlePostponedDecisionsUntilNoChange();
            proposerSignalsIn.observe(new RestartProposer());
            return CandidateDecision.Discard;
        }
        return CandidateDecision.Postpone;
    }

    @Override
    protected boolean handlePostponedDecisions() {
        if (collectedPlaces.isEmpty()) return false;
        Random random = new Random();
        int i = random.nextInt(collectedPlaces.size());
        Place lucky = collectedPlaces.remove(i);
        acceptCandidate(lucky);
        collectedPlaces.clear();
        return true;
    }

    @Override
    protected void postponeDecision(Place candidate) {
        collectedPlaces.add(candidate);
    }

    @Override
    protected void rejectCandidate(Place candidate) {

    }

    @Override
    protected void discardCandidate(Place candidate) {

    }

    @Override
    public Class<CandidateConstraint<Place>> getPublishedConstraintClass() {
        return JavaTypingUtils.castClass(CandidateConstraint.class);
    }

    @Override
    protected void initSelf() {

    }
}
