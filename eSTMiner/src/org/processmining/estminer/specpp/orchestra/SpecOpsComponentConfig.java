package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.data.DataSourceCollection;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.config.GeneratingTreeConfiguration;
import org.processmining.estminer.specpp.config.PostProcessingConfiguration;
import org.processmining.estminer.specpp.config.ProposerComposerConfiguration;
import org.processmining.estminer.specpp.config.SupervisionConfiguration;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.estminer.specpp.datastructures.tree.base.PlaceGenerator;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;

public interface SpecOpsComponentConfig {

    default void registerConfigurations(GlobalComponentRepository cr) {
        ComponentCollection csa = cr.componentSystemAdapter();
        DataSourceCollection dc = cr.dataSources();
        dc.register(DataRequirements.EVALUATOR_CONFIG, StaticDataSource.of(getEvaluatorConfiguration(csa)));
        dc.register(DataRequirements.SUPERVISOR_CONFIG, StaticDataSource.of(getSupervisionConfiguration(csa)));
        dc.register(DataRequirements.proposerComposerConfiguration(), StaticDataSource.of(getProposerComposerConfiguration(csa)));
        dc.register(DataRequirements.postprocessingConfiguration(), StaticDataSource.of(getPostProcessingConfiguration(csa)));
        dc.register(DataRequirements.generatingTreeConfiguration(), StaticDataSource.of(getGeneratingTreeConfiguration(csa)));
    }

    EvaluatorConfiguration getEvaluatorConfiguration(ComponentCollection csa);

    SupervisionConfiguration getSupervisionConfiguration(ComponentCollection csa);

    ProposerComposerConfiguration<Place, AdvancedComposition<Place>, PetriNet> getProposerComposerConfiguration(ComponentCollection csa);

    PostProcessingConfiguration<PetriNet, ProMPetrinetWrapper> getPostProcessingConfiguration(ComponentCollection csa);

    GeneratingTreeConfiguration<PlaceNode, PlaceGenerator> getGeneratingTreeConfiguration(ComponentCollection csa);

}
