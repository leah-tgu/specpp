package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.config.parameters.PlaceFocusParameters;

public class PlaceFocusedSpecOpsConfigBundle extends SpecOpsConfigBundle {
    @Override
    public String getTitle() {
        return "Place Focused Variant";
    }

    @Override
    public String getDescription() {
        return "This configuration employs the most lightweight components as possible to focus on merely discovering fitting places, not an implicit place-free Petri net.";
    }

    public PlaceFocusedSpecOpsConfigBundle() {
        super(new BaseSpecOpsDataPreprocessingConfig(), new PlaceFocussedComponentConfig(), new AdaptedSpecOpsAlgorithmParameterConfig(new PlaceFocusParameters()));
    }
}
