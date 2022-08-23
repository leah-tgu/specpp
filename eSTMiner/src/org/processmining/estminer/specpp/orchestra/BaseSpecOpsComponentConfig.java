package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.composition.EventingPlacesComposerWithCIPR;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.config.*;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.estminer.specpp.datastructures.tree.base.PlaceGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.EventingEnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.EventingHeuristicTreeExpansion;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.HeuristicUtils;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
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
    public SupervisionConfiguration getSupervisionConfiguration(ComponentCollection csa) {
        return Configurators.supervisors()
                            .supervisor(BaseSupervisor::new)
                            .supervisor(PerformanceSupervisor::new)
                            .supervisor(AltEventCountsSupervisor::new)
                            .supervisor(DetailedCompositionSupervisor::new)
                            .supervisor(TerminalSupervisor::new)
                            .build(csa);
    }

    @Override
    public EvaluatorConfiguration getEvaluatorConfiguration(ComponentCollection csa) {
        return Configurators.evaluators()
                            .evaluatorProvider(LogHistoryMaker::new)
                            .evaluatorProvider(AbsolutelyNoFrillsFitnessEvaluator::new)
                            .build(csa);
    }

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, PetriNet> getProposerComposerConfiguration(ComponentCollection csa) {
        return Configurators.<Place, AdvancedComposition<Place>, PetriNet>proposerComposer()
                            .proposer(new ConstrainablePlaceProposer.Builder())
                            .composition(PlaceCollection::new)
                            .composer(EventingPlacesComposerWithCIPR::new)
                            .build(csa);
    }

    @Override
    public EfficientTreeConfiguration<PlaceNode, PlaceGenerationLogic> getGeneratingTreeConfiguration(ComponentCollection csa) {
        return Configurators.<PlaceNode, PlaceGenerationLogic, DoubleScore>heuristicTree()
                            .heuristic(HeuristicUtils::bfs)
                            .heuristicExpansion(EventingHeuristicTreeExpansion::new)
                            .enumeratingTree(EventingEnumeratingTree::new)
                            .constrainableGenerator(new MonotonousPlaceGenerationLogic.Builder())
                            .build(csa);
    }

    @Override
    public PostProcessingConfiguration<PetriNet, ProMPetrinetWrapper> getPostProcessingConfiguration(ComponentCollection csa) {
        return Configurators.<PetriNet>postProcessing()
                            .instrumentedProcessor("ReplayBasedImplicitness", new ReplayBasedImplicitnessPostProcessing.Builder())
                            .instrumentedProcessor("SelfLoopPlaceMerger", SelfLoopPlaceMerger::new)
                            .processor(PlaceExporter::new)
                            .processor(ProMConverter::new)
                            .build(csa);
    }
}
