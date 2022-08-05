package org.processmining.estminer.specpp.base;

import org.processmining.estminer.specpp.traits.IsSizeLimited;

public interface MutableCappedComposition<C extends Candidate> extends Composition<C>, MutableSequentialCollection<C>, IsSizeLimited {
}
