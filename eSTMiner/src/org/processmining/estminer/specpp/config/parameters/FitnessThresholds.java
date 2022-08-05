package org.processmining.estminer.specpp.config.parameters;

public class FitnessThresholds implements Parameters {

    public static FitnessThresholds strictUnderfedCulling(double replayableThreshold) {
        return new FitnessThresholds(replayableThreshold, 1e-8);
    }

    public static FitnessThresholds exhaustive(double replayableThreshold) {
        return new FitnessThresholds(replayableThreshold, 1);
    }

    private final double replayableFractionAcceptanceThreshold, underfedFractionCullingThreshold;

    public FitnessThresholds(double replayableFractionAcceptanceThreshold, double underfedFractionCullingThreshold) {
        this.replayableFractionAcceptanceThreshold = replayableFractionAcceptanceThreshold;
        this.underfedFractionCullingThreshold = underfedFractionCullingThreshold;
    }

    public double getReplayableFractionAcceptanceThreshold() {
        return replayableFractionAcceptanceThreshold;
    }

    public double getUnderfedFractionCullingThreshold() {
        return underfedFractionCullingThreshold;
    }
}
