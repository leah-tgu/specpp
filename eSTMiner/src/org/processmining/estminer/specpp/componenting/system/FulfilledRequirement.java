package org.processmining.estminer.specpp.componenting.system;

public interface FulfilledRequirement<C, R> extends Requirement<C, R> {

    C getContent();

    R getComparable();

}
