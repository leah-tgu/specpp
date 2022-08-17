package org.processmining.estminer.specpp.base;

import org.processmining.estminer.specpp.base.impls.CandidateConstraint;

public interface ConstrainableProposer<C extends Candidate, L extends CandidateConstraint<C>> extends Proposer<C>, Constrainable<L> {

}
