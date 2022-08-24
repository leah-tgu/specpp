package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.componenting.data.DataRequirements;
import org.processmining.estminer.specpp.componenting.data.DataSourceCollection;
import org.processmining.estminer.specpp.componenting.data.StaticDataSource;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.config.EfficientTreeConfiguration;
import org.processmining.estminer.specpp.config.PostProcessingConfiguration;
import org.processmining.estminer.specpp.config.ProposerComposerConfiguration;
import org.processmining.estminer.specpp.config.SupervisionConfiguration;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceState;

public interface SpecOpsComponentConfig {

    default void registerConfigurations(GlobalComponentRepository cr) {
        DataSourceCollection dc = cr.dataSources();
        dc.register(DataRequirements.EVALUATOR_CONFIG, StaticDataSource.of(getEvaluatorConfiguration(cr)));
        dc.register(DataRequirements.SUPERVISOR_CONFIG, StaticDataSource.of(getSupervisionConfiguration(cr)));
        dc.register(DataRequirements.proposerComposerConfiguration(), StaticDataSource.of(getProposerComposerConfiguration(cr)));
        dc.register(DataRequirements.postprocessingConfiguration(), StaticDataSource.of(getPostProcessingConfiguration(cr)));
        dc.register(DataRequirements.efficientTreeConfiguration(), StaticDataSource.of(getEfficientTreeConfiguration(cr)));
    }

    EvaluatorConfiguration getEvaluatorConfiguration(GlobalComponentRepository csa);

    SupervisionConfiguration getSupervisionConfiguration(GlobalComponentRepository csa);

    ProposerComposerConfiguration<Place, AdvancedComposition<Place>, PetriNet> getProposerComposerConfiguration(GlobalComponentRepository csa);

    PostProcessingConfiguration<PetriNet, ProMPetrinetWrapper> getPostProcessingConfiguration(GlobalComponentRepository csa);

    EfficientTreeConfiguration<Place, PlaceState, PlaceNode> getEfficientTreeConfiguration(GlobalComponentRepository csa);

}
