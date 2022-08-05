package org.processmining.estminer.specpp.supervision;

import org.processmining.estminer.specpp.componenting.traits.IsGlobalProvider;
import org.processmining.estminer.specpp.componenting.traits.ProvidesSupervisors;
import org.processmining.estminer.specpp.traits.Initializable;
import org.processmining.estminer.specpp.traits.StartStoppable;

public interface Supervisor extends Initializable, StartStoppable, ProvidesSupervisors, IsGlobalProvider {


}
