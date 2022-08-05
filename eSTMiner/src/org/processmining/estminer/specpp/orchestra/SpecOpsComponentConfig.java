package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.data.DataSourceCollection;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.estminer.specpp.componenting.system.ComponentRepository;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.config.GeneratingTreeConfiguration;
import org.processmining.estminer.specpp.config.PostProcessingConfiguration;
import org.processmining.estminer.specpp.config.ProposerComposerConfiguration;
import org.processmining.estminer.specpp.config.SupervisionConfiguration;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceGenerator;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;

import static org.processmining.estminer.specpp.componenting.data.StaticDataSource.of;

public interface SpecOpsComponentConfig {

    default void registerConfigurations(ComponentRepository cr) {
        ComponentSystemAdapter csa = cr.componentSystemAdapter();
        DataSourceCollection dc = cr.dataSources();
        dc.register(DataRequirements.EVALUATOR_CONFIG, of(getEvaluatorConfiguration(csa)));
        dc.register(DataRequirements.SUPERVISOR_CONFIG, of(getSupervisionConfiguration(csa)));
        dc.register(DataRequirements.proposerComposerConfiguration(), of(getProposerComposerConfiguration(csa)));
        dc.register(DataRequirements.postprocessingConfiguration(), of(getPostProcessingConfiguration(csa)));
        dc.register(DataRequirements.generatingTreeConfiguration(), of(getGeneratingTreeConfiguration(csa)));
    }

    EvaluatorConfiguration getEvaluatorConfiguration(ComponentSystemAdapter csa);

    SupervisionConfiguration getSupervisionConfiguration(ComponentSystemAdapter csa);

    ProposerComposerConfiguration<Place, PlaceCollection, PetriNet> getProposerComposerConfiguration(ComponentSystemAdapter csa);

    PostProcessingConfiguration<PetriNet, ProMPetrinetWrapper> getPostProcessingConfiguration(ComponentSystemAdapter csa);

    GeneratingTreeConfiguration<PlaceNode, PlaceGenerator> getGeneratingTreeConfiguration(ComponentSystemAdapter csa);

}
