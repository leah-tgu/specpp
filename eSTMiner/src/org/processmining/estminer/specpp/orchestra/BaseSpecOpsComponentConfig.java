package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.base.impls.EventingPlaceComposerWithCIPR;
import org.processmining.estminer.specpp.base.impls.EventingPlaceFitnessFilter;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.config.*;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.EventingEnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.EventingHeuristicTreeExpansion;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.HeuristicUtils;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.estminer.specpp.evaluation.fitness.AbsolutelyNoFrillsFitnessEvaluator;
import org.processmining.estminer.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.estminer.specpp.postprocessing.PlaceExporter;
import org.processmining.estminer.specpp.postprocessing.ProMConverter;
import org.processmining.estminer.specpp.postprocessing.ReplayBasedImplicitnessPostProcessing;
import org.processmining.estminer.specpp.postprocessing.SelfLoopPlaceMerger;
import org.processmining.estminer.specpp.proposal.ConstrainablePlaceProposer;
import org.processmining.estminer.specpp.supervision.supervisors.*;

public class BaseSpecOpsComponentConfig implements SpecOpsComponentConfig {

    @Override
    public SupervisionConfiguration getSupervisionConfiguration(GlobalComponentRepository gcr) {
        return Configurators.supervisors()
                            .supervisor(BaseSupervisor::new)
                            .supervisor(PerformanceSupervisor::new)
                            .supervisor(AltEventCountsSupervisor::new)
                            .supervisor(DetailedComposerSupervisor::new)
                            .supervisor(TerminalSupervisor::new)
                            .build(gcr);
    }

    @Override
    public EvaluatorConfiguration getEvaluatorConfiguration(GlobalComponentRepository gcr) {
        return Configurators.evaluators()
                            .evaluatorProvider(LogHistoryMaker::new)
                            .evaluatorProvider(AbsolutelyNoFrillsFitnessEvaluator::new)
                            .build(gcr);
    }

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, PetriNet> getProposerComposerConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, AdvancedComposition<Place>, PetriNet>proposerComposer()
                            .proposer(new ConstrainablePlaceProposer.Builder())
                            .composition(PlaceCollection::new)
                            .terminalComposer(EventingPlaceComposerWithCIPR::new)
                            .composerChain(EventingPlaceFitnessFilter::new)
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
                            .processor(new ReplayBasedImplicitnessPostProcessing.Builder())
                            .processor(SelfLoopPlaceMerger::new)
                            .processor(new PlaceExporter.Builder())
                            .processor(ProMConverter::new)
                            .build(gcr);
    }
}
