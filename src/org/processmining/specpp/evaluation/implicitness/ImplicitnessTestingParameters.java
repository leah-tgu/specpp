package org.processmining.specpp.evaluation.implicitness;

import org.processmining.specpp.config.parameters.Parameters;

public class ImplicitnessTestingParameters implements Parameters {

    private final SubLogRestriction subLogRestriction;

    public ImplicitnessTestingParameters(SubLogRestriction subLogRestriction) {
        this.subLogRestriction = subLogRestriction;
    }

    public SubLogRestriction getSubLogRestriction() {
        return subLogRestriction;
    }

    public static ImplicitnessTestingParameters getDefault() {
        return new ImplicitnessTestingParameters(SubLogRestriction.None);
    }

    public enum SubLogRestriction {
        None, FittingOnAcceptedPlacesAndEvaluatedPlace, MerelyFittingOnEvaluatedPair
    }

}
