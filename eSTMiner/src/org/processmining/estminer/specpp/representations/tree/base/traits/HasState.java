package org.processmining.estminer.specpp.representations.tree.base.traits;

import org.processmining.estminer.specpp.representations.tree.base.NodeState;

public interface HasState<S extends NodeState> {

    void setState(S state);

    S getState();

}
