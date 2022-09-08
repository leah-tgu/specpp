package org.processmining.specpp.componenting.system.link;

import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.Composition;
import org.processmining.specpp.componenting.system.FullComponentSystemUser;

import java.util.List;

public interface CompositionComponent<C extends Candidate> extends Composition<C>, FullComponentSystemUser {
}
