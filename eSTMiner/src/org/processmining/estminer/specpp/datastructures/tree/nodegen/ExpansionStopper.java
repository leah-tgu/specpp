package org.processmining.estminer.specpp.datastructures.tree.nodegen;

public interface ExpansionStopper {

    boolean notAllowedToExpand(PlaceNode placeNode);

}
