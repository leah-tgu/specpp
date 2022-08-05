package org.processmining.estminer.specpp.datastructures.vectorization;

@FunctionalInterface
public interface BooleanBinaryOperator {

    boolean test(boolean a, boolean b);

}
