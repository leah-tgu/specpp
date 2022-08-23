package org.processmining.estminer.specpp.componenting.system.link;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.Proposer;
import org.processmining.estminer.specpp.componenting.system.FullComponentSystemUser;

public interface ProposerComponent<C extends Candidate> extends Proposer<C>, FullComponentSystemUser {
}
