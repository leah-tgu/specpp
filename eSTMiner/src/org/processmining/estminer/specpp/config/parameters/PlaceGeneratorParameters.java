package org.processmining.estminer.specpp.config.parameters;

public class PlaceGeneratorParameters implements Parameters {

    private final int maxTreeDepth;

    public PlaceGeneratorParameters(int maxTreeDepth) {
        this.maxTreeDepth = maxTreeDepth;
    }

    public static PlaceGeneratorParameters getDefault() {
        return new PlaceGeneratorParameters(Integer.MAX_VALUE);
    }

    public int getMaxTreeDepth() {
        return maxTreeDepth;
    }
}
