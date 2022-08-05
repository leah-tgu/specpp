package org.processmining.estminer.specpp.representations.tree.base.traits;

import org.processmining.estminer.specpp.representations.tree.base.NodeProperties;

public interface HasProperties<P extends NodeProperties> {

    P getProperties();

}
