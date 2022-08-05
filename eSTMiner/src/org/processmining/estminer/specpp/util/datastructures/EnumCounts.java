package org.processmining.estminer.specpp.util.datastructures;

import java.util.Arrays;

public class EnumCounts<E extends Enum<E>> {

    public final int[] counts;

    public EnumCounts(int[] counts) {
        this.counts = counts;
    }

    public double getCount(E enumInstance) {
        return counts[enumInstance.ordinal()];
    }

    @Override
    public String toString() {
        return "EnumCounts{" + Arrays.toString(counts) + "}";
    }

}
