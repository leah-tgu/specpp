package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.composition.PlacesComposerWithCIPR;
import org.processmining.estminer.specpp.config.Configurators;
import org.processmining.estminer.specpp.config.EfficientTreeConfiguration;
import org.processmining.estminer.specpp.config.ProposerComposerConfiguration;
import org.processmining.estminer.specpp.config.SupervisionConfiguration;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.base.PlaceGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.VariableExpansion;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.proposal.ConstrainablePlaceProposer;
import org.processmining.estminer.specpp.supervision.supervisors.BaseSupervisor;
import org.processmining.estminer.specpp.supervision.supervisors.PerformanceSupervisor;
import org.processmining.estminer.specpp.supervision.supervisors.TerminalSupervisor;

public class LightweightSpecOpsComponentConfig extends BaseSpecOpsComponentConfig {

    @Override
    public SupervisionConfiguration getSupervisionConfiguration(ComponentCollection csa) {
        return Configurators.supervisors()
                            .supervisor(BaseSupervisor::new)
                            .supervisor(PerformanceSupervisor::new)
                            .supervisor(TerminalSupervisor::new)
                            .build(csa);
    }

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, PetriNet> getProposerComposerConfiguration(ComponentCollection csa) {
        return Configurators.<Place, AdvancedComposition<Place>, PetriNet>proposerComposer()
                            .proposer(new ConstrainablePlaceProposer.Builder())
                            .composition(PlaceCollection::new)
                            .composer(PlacesComposerWithCIPR::new)
                            .build(csa);
    }

    @Override
    public EfficientTreeConfiguration<PlaceNode, PlaceGenerationLogic> getGeneratingTreeConfiguration(ComponentCollection csa) {
        return Configurators.<PlaceNode, PlaceGenerationLogic>generatingTree()
                            .childGenerationLogic(new MonotonousPlaceGenerationLogic.Builder())
                            .expansionStrategy(VariableExpansion::bfs)
                            .tree(EnumeratingTree::new)
                            .build(csa);
    }

}
