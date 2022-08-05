package org.processmining.estminer.specpp.base;

import org.processmining.estminer.specpp.supervision.observations.EvaluatorUpdateEvent;
import org.processmining.estminer.specpp.supervision.piping.Observer;

public interface ListeningEvaluator<C extends Candidate, E extends CandidateEvaluation, L extends EvaluatorUpdateEvent> extends CandidateEvaluator<C, E>, Observer<L> {
}
