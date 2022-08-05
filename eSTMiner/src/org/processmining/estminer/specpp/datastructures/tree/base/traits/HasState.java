package org.processmining.estminer.specpp.datastructures.tree.base.traits;

import org.processmining.estminer.specpp.datastructures.tree.base.NodeState;

public interface HasState<S extends NodeState> {

    void setState(S state);

    S getState();

}
