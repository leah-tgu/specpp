package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.base.impls.LightweightPlaceCollection;
import org.processmining.estminer.specpp.base.impls.PlaceAccepter;
import org.processmining.estminer.specpp.base.impls.PlaceFitnessFilter;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.config.Configurators;
import org.processmining.estminer.specpp.config.PostProcessingConfiguration;
import org.processmining.estminer.specpp.config.ProposerComposerConfiguration;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.estminer.specpp.postprocessing.PlaceExporter;
import org.processmining.estminer.specpp.postprocessing.ProMConverter;
import org.processmining.estminer.specpp.proposal.ConstrainablePlaceProposer;

public class PlaceFocussedComponentConfig extends LightweightComponentConfig {
    @Override
    public PostProcessingConfiguration<PetriNet, ProMPetrinetWrapper> getPostProcessingConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<PetriNet>postProcessing()
                            .processor(new PlaceExporter.Builder())
                            .processor(ProMConverter::new)
                            .build(gcr);
    }

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, PetriNet> getProposerComposerConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, AdvancedComposition<Place>, PetriNet>proposerComposer()
                            .proposer(new ConstrainablePlaceProposer.Builder())
                            .composition(LightweightPlaceCollection::new)
                            .composer(PlaceAccepter::new)
                            .composerChain(PlaceFitnessFilter::new)
                            .build(gcr);
    }

}
