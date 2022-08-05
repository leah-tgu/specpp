package org.processmining.estminer.specpp.base;

import org.processmining.estminer.specpp.traits.HasCapacity;

public interface MutableCappedComposition<C extends Candidate> extends Composition<C>, MutableSequentialCollection<C>, HasCapacity {
}
