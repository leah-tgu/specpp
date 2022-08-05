package org.processmining.estminer.specpp.representations.petri;

import org.processmining.estminer.specpp.base.PostProcessor;

public class ProMConverter implements PostProcessor<PetriNet, ProMPetrinetWrapper> {
    @Override
    public ProMPetrinetWrapper postProcess(PetriNet result) {
        ProMPetrinetBuilder builder = new ProMPetrinetBuilder(result.getPlaces());
        return builder.build();
    }
}
