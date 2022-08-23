package org.processmining.estminer.specpp.base;

import org.processmining.estminer.specpp.componenting.system.link.CompositionComponent;
import org.processmining.estminer.specpp.datastructures.util.MutableSequentialCollection;
import org.processmining.estminer.specpp.traits.IsSizeLimited;

public interface MutableCappedComposition<C extends Candidate> extends CompositionComponent<C>, MutableSequentialCollection<C>, IsSizeLimited {
}
