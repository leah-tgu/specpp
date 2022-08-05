package org.processmining.estminer.specpp.representations.tree.base;

import org.processmining.estminer.specpp.representations.tree.base.impls.GeneratingLocalNode;
import org.processmining.estminer.specpp.representations.tree.base.traits.Constrainable;

public interface ConstrainableLocalNodeGenerator<P extends NodeProperties, S extends NodeState, N extends GeneratingLocalNode<P, S, N>, L extends GenerationConstraint> extends LocalNodeGenerator<P, S, N>, Constrainable<L> {
}
