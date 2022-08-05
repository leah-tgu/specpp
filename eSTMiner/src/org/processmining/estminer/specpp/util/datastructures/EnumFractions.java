package org.processmining.estminer.specpp.util.datastructures;

import java.util.Arrays;

public class EnumFractions<E extends Enum<E>> {

    public final double[] fractions;

    public EnumFractions(double[] fractions) {
        this.fractions = fractions;
    }

    public double getFraction(E enumInstance) {
        return fractions[enumInstance.ordinal()];
    }

    @Override
    public String toString() {
        return "EnumFractions{" + Arrays.toString(fractions) + "}";
    }

}
