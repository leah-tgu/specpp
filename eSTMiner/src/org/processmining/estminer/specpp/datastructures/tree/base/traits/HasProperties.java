package org.processmining.estminer.specpp.datastructures.tree.base.traits;

import org.processmining.estminer.specpp.datastructures.tree.base.NodeProperties;

public interface HasProperties<P extends NodeProperties> {

    P getProperties();

}
