package org.processmining.estminer.specpp.postprocessing;

import org.processmining.estminer.specpp.base.PostProcessor;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetBuilder;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;

public class ProMConverter implements PostProcessor<PetriNet, ProMPetrinetWrapper> {
    @Override
    public ProMPetrinetWrapper postProcess(PetriNet result) {
        ProMPetrinetBuilder builder = new ProMPetrinetBuilder(result.getPlaces());
        return builder.build();
    }

}
