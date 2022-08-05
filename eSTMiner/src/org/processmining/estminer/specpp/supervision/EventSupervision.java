package org.processmining.estminer.specpp.supervision;

import org.processmining.estminer.specpp.supervision.observations.Event;
import org.processmining.estminer.specpp.supervision.piping.IdentityPipe;
import org.processmining.estminer.specpp.supervision.traits.OneToOne;

public class EventSupervision<E extends Event> extends IdentityPipe<E> implements OneToOne<E, E> {
}
