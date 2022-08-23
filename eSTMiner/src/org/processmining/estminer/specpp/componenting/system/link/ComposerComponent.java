package org.processmining.estminer.specpp.componenting.system.link;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.Composer;
import org.processmining.estminer.specpp.base.Result;
import org.processmining.estminer.specpp.componenting.system.FullComponentSystemUser;

public interface ComposerComponent<C extends Candidate, I extends CompositionComponent<C>, R extends Result> extends Composer<C, I, R>, FullComponentSystemUser {
}
