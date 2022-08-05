package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.estminer.specpp.componenting.system.ComponentSystemAdapter;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.composition.PlaceComposerWithConcurrentImplicitnessTesting;
import org.processmining.estminer.specpp.config.*;
import org.processmining.estminer.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.InstrumentedEnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.HeuristicUtils;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.InstrumentedHeuristicTreeExpansion;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceGenerator;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.evaluation.fitness.MarkingHistoryBasedFitnessEvaluator;
import org.processmining.estminer.specpp.postprocessing.ProMConverter;
import org.processmining.estminer.specpp.postprocessing.ReplayBasedImplicitnessPostProcessing;
import org.processmining.estminer.specpp.proposal.ConstrainablePlaceProposer;
import org.processmining.estminer.specpp.supervision.supervisors.*;

public class BaseSpecOpsComponentConfig implements SpecOpsComponentConfig {

    @Override
    public SupervisionConfiguration getSupervisionConfiguration(ComponentSystemAdapter csa) {
        return Configurators.supervisors()
                            .supervisor(BaseSupervisor::new)
                            .supervisor(DetailedHeuristicsSupervisor::new)
                            .supervisor(DetailedTreeSupervisor::new)
                            .supervisor(DebuggingSupervisor::new)
                            .supervisor(ProposalTreeSupervisor::new)
                            .supervisor(TerminalSupervisor::new)
                            .build(csa);
    }

    @Override
    public EvaluatorConfiguration getEvaluatorConfiguration(ComponentSystemAdapter csa) {
        return Configurators.evaluators()
                            .evaluatorProvider(LogHistoryMaker::new)
                            .evaluatorProvider(MarkingHistoryBasedFitnessEvaluator::new)
                            .build(csa);
    }

    @Override
    public ProposerComposerConfiguration<Place, PlaceCollection, PetriNet> getProposerComposerConfiguration(ComponentSystemAdapter csa) {
        return Configurators.<Place, PlaceCollection, PetriNet>proposerComposer()
                            .proposer(new ConstrainablePlaceProposer.Builder())
                            .composition(PlaceCollection::new)
                            .composer(PlaceComposerWithConcurrentImplicitnessTesting::new)
                            .build(csa);
    }

    @Override
    public GeneratingTreeConfiguration<PlaceNode, PlaceGenerator> getGeneratingTreeConfiguration(ComponentSystemAdapter csa) {
        return Configurators.<PlaceNode, PlaceGenerator, DoubleScore>heuristicTree()
                            .heuristic(HeuristicUtils::bfs)
                            .heuristicExpansion(InstrumentedHeuristicTreeExpansion::new)
                            .enumeratingTree(InstrumentedEnumeratingTree::new)
                            .constrainableGenerator(new PlaceGenerator.Builder())
                            .build(csa);
    }

    @Override
    public PostProcessingConfiguration<PetriNet, ProMPetrinetWrapper> getPostProcessingConfiguration(ComponentSystemAdapter csa) {
        return Configurators.<PetriNet>postProcessing()
                            .instrumentedProcessor("ReplayBasedImplicitness", new ReplayBasedImplicitnessPostProcessing.Builder())
                            .processor(ProMConverter::new)
                            .build(csa);
    }
}
