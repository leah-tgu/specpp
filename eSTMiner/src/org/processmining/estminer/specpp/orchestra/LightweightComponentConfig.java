package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.base.impls.PlaceComposerWithCIPR;
import org.processmining.estminer.specpp.base.impls.PlaceFitnessFilter;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.config.Configurators;
import org.processmining.estminer.specpp.config.EfficientTreeConfiguration;
import org.processmining.estminer.specpp.config.ProposerComposerConfiguration;
import org.processmining.estminer.specpp.config.SupervisionConfiguration;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.VariableExpansion;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.estminer.specpp.proposal.ConstrainablePlaceProposer;
import org.processmining.estminer.specpp.supervision.supervisors.BaseSupervisor;
import org.processmining.estminer.specpp.supervision.supervisors.PerformanceSupervisor;
import org.processmining.estminer.specpp.supervision.supervisors.TerminalSupervisor;

public class LightweightComponentConfig extends BaseSpecOpsComponentConfig {

    @Override
    public SupervisionConfiguration getSupervisionConfiguration(GlobalComponentRepository gcr) {
        return Configurators.supervisors()
                            .supervisor(BaseSupervisor::new)
                            .supervisor(PerformanceSupervisor::new)
                            .supervisor(TerminalSupervisor::new)
                            .build(gcr);
    }

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, PetriNet> getProposerComposerConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, AdvancedComposition<Place>, PetriNet>proposerComposer()
                            .proposer(new ConstrainablePlaceProposer.Builder())
                            .composition(PlaceCollection::new)
                            .composer(PlaceComposerWithCIPR::new)
                            .composerChain(PlaceFitnessFilter::new)
                            .build(gcr);
    }

    @Override
    public EfficientTreeConfiguration<Place, PlaceState, PlaceNode> getEfficientTreeConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, PlaceState, PlaceNode>generatingTree()
                            .childGenerationLogic(new MonotonousPlaceGenerationLogic.Builder())
                            .expansionStrategy(VariableExpansion::dfs)
                            .tree(EnumeratingTree::new)
                            .build(gcr);
    }

}
