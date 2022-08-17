package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.composition.PlacesComposerWithCPR;
import org.processmining.estminer.specpp.config.Configurators;
import org.processmining.estminer.specpp.config.GeneratingTreeConfiguration;
import org.processmining.estminer.specpp.config.ProposerComposerConfiguration;
import org.processmining.estminer.specpp.config.SupervisionConfiguration;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.base.PlaceGenerator;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.VariableExpansion;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerator;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.proposal.ConstrainablePlaceProposer;
import org.processmining.estminer.specpp.supervision.supervisors.BaseSupervisor;
import org.processmining.estminer.specpp.supervision.supervisors.PerformanceSupervisor;
import org.processmining.estminer.specpp.supervision.supervisors.TerminalSupervisor;

public class LightweightSpecOpsComponentConfig extends BaseSpecOpsComponentConfig {

    @Override
    public SupervisionConfiguration getSupervisionConfiguration(ComponentSystemAdapter csa) {
        return Configurators.supervisors()
                            .supervisor(BaseSupervisor::new)
                            .supervisor(PerformanceSupervisor::new)
                            .supervisor(TerminalSupervisor::new)
                            .build(csa);
    }

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, PetriNet> getProposerComposerConfiguration(ComponentSystemAdapter csa) {
        return Configurators.<Place, AdvancedComposition<Place>, PetriNet>proposerComposer()
                            .proposer(new ConstrainablePlaceProposer.Builder())
                            .composition(PlaceCollection::new)
                            .composer(PlacesComposerWithCPR::new)
                            .build(csa);
    }

    @Override
    public GeneratingTreeConfiguration<PlaceNode, PlaceGenerator> getGeneratingTreeConfiguration(ComponentSystemAdapter csa) {
        return Configurators.<PlaceNode, PlaceGenerator>generatingTree()
                            .generator(new MonotonousPlaceGenerator.Builder())
                            .expansionStrategy(VariableExpansion::bfs)
                            .tree(EnumeratingTree::new)
                            .build(csa);
    }

}
