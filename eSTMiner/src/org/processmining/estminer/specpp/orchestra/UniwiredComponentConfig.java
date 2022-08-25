package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.base.impls.LightweightPlaceCollection;
import org.processmining.estminer.specpp.base.impls.PlaceAccepter;
import org.processmining.estminer.specpp.base.impls.PlaceFitnessFilter;
import org.processmining.estminer.specpp.base.impls.UniwiredComposer;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.composition.ConstrainingPlaceCollection;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.config.Configurators;
import org.processmining.estminer.specpp.config.EfficientTreeConfiguration;
import org.processmining.estminer.specpp.config.PostProcessingConfiguration;
import org.processmining.estminer.specpp.config.ProposerComposerConfiguration;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.EventingEnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.TestEventingEnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.EventingHeuristicTreeExpansion;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.HeuristicUtils;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.estminer.specpp.postprocessing.ProMConverter;
import org.processmining.estminer.specpp.proposal.RestartablePlaceProposer;

public class UniwiredComponentConfig extends BaseSpecOpsComponentConfig {

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, PetriNet> getProposerComposerConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, AdvancedComposition<Place>, PetriNet>proposerComposer()
                            .proposer(new RestartablePlaceProposer.Builder())
                            .composition(ConstrainingPlaceCollection::new)
                            .terminalComposer(PlaceAccepter::new)
                            .composerChain(PlaceFitnessFilter::new, UniwiredComposer::new)
                            .build(gcr);
    }

    @Override
    public EfficientTreeConfiguration<Place, PlaceState, PlaceNode> getEfficientTreeConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, PlaceState, PlaceNode, DoubleScore>heuristicTree()
                            .heuristic(HeuristicUtils::bfs)
                            .heuristicExpansion(EventingHeuristicTreeExpansion::new)
                            .tree(EventingEnumeratingTree::new)
                            .childGenerationLogic(new MonotonousPlaceGenerationLogic.Builder())
                            .build(gcr);
    }

    @Override
    public PostProcessingConfiguration<PetriNet, ProMPetrinetWrapper> getPostProcessingConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<PetriNet>postProcessing()
                            //.instrumentedProcessor("ReplayBasedImplicitness", new ReplayBasedImplicitnessPostProcessing.Builder())
                            //.instrumentedProcessor("SelfLoopPlaceMerger", SelfLoopPlaceMerger::new)
                            .processor(ProMConverter::new).build(gcr);
    }

}
