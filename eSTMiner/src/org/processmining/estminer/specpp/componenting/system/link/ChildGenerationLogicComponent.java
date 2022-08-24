package org.processmining.estminer.specpp.componenting.system.link;

import org.processmining.estminer.specpp.componenting.system.FullComponentSystemUser;
import org.processmining.estminer.specpp.datastructures.tree.base.ChildGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.base.LocalNode;
import org.processmining.estminer.specpp.datastructures.tree.base.NodeProperties;
import org.processmining.estminer.specpp.datastructures.tree.base.NodeState;

public interface ChildGenerationLogicComponent<P extends NodeProperties, S extends NodeState, N extends LocalNode<P, S, N>> extends ChildGenerationLogic<P, S, N>, FullComponentSystemUser {
}
