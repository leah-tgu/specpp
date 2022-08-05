package org.processmining.estminer.specpp.base;

import org.processmining.estminer.specpp.base.impls.CandidateConstraint;
import org.processmining.estminer.specpp.representations.tree.base.traits.Constrainable;

public interface ConstrainableProposer<C extends Candidate, L extends CandidateConstraint<C>> extends Proposer<C>, Constrainable<L> {

}
