package org.processmining.estminer.specpp.datastructures.tree.base;

import org.processmining.estminer.specpp.base.Constrainable;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.LocalNodeWithExternalizedLogic;

public interface ConstrainableChildGenerationLogic<P extends NodeProperties, S extends NodeState, N extends LocalNodeWithExternalizedLogic<P, S, N>, L extends GenerationConstraint> extends ChildGenerationLogic<P, S, N>, Constrainable<L> {
}
