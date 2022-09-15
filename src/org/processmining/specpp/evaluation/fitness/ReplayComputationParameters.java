package org.processmining.specpp.evaluation.fitness;

import org.processmining.specpp.config.parameters.Parameters;

public class ReplayComputationParameters implements Parameters {

    private final boolean clipMarkingAtZero;

    public ReplayComputationParameters(boolean clipMarkingAtZero) {
        this.clipMarkingAtZero = clipMarkingAtZero;
    }

    public boolean isClipMarkingAtZero() {
        return clipMarkingAtZero;
    }
}
