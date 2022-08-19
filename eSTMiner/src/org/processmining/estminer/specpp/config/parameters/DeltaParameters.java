package org.processmining.estminer.specpp.config.parameters;

public class DeltaParameters implements Parameters {

    private final double delta;

    public DeltaParameters(double delta) {
        this.delta = delta;
    }

    public static DeltaParameters delta(double d) {
        return new DeltaParameters(d);
    }

    public static DeltaParameters getDefault() {
        return new DeltaParameters(1);
    }

}
