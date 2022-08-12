package org.processmining.estminer.specpp.config.parameters;

public class TauFitnessThresholds implements Parameters {

    public static TauFitnessThresholds tau(int t) {
        return new TauFitnessThresholds(t, 1 - t, 1 - t);
    }

    public static TauFitnessThresholds getDefault() {
        return tau(1);
    }

    private final double fitting, underfed, overfed;

    public TauFitnessThresholds(double fitting, double underfed, double overfed) {
        this.fitting = fitting;
        this.underfed = underfed;
        this.overfed = overfed;
    }

    public double getFittingThreshold() {
        return fitting;
    }

    public double getUnderfedThreshold() {
        return underfed;
    }

    public double getOverfedThreshold() {
        return overfed;
    }


    @Override
    public String toString() {
        return "TauFitnessThresholds(\uD835\uDED5=" + fitting +")";
    }
}
