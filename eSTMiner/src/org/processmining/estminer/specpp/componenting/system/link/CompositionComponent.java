package org.processmining.estminer.specpp.componenting.system.link;

import org.processmining.estminer.specpp.base.Candidate;
import org.processmining.estminer.specpp.base.Composition;
import org.processmining.estminer.specpp.componenting.system.FullComponentSystemUser;

public interface CompositionComponent<C extends Candidate> extends Composition<C>, FullComponentSystemUser {
}
