package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.base.impls.PlaceAccepter;
import org.processmining.estminer.specpp.base.impls.PlaceFitnessFilter;
import org.processmining.estminer.specpp.base.impls.QueueingPostponingPlaceComposer;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.estminer.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.config.Configurators;
import org.processmining.estminer.specpp.config.EfficientTreeConfiguration;
import org.processmining.estminer.specpp.config.ProposerComposerConfiguration;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.EventingEnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.EventingHeuristicTreeExpansion;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.HeuristicUtils;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.estminer.specpp.evaluation.fitness.AbsolutelyNoFrillsFitnessEvaluator;
import org.processmining.estminer.specpp.evaluation.heuristics.DeltaAdaptationFunction;
import org.processmining.estminer.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.estminer.specpp.proposal.ConstrainablePlaceProposer;

public class TauDeltaComponentConfig extends BaseSpecOpsComponentConfig {

    @Override
    public EvaluatorConfiguration getEvaluatorConfiguration(GlobalComponentRepository gcr) {
        return Configurators.evaluators()
                            .evaluatorProvider(LogHistoryMaker::new)
                            .evaluatorProvider(AbsolutelyNoFrillsFitnessEvaluator::new)
                            .evaluatorProvider(new DeltaAdaptationFunction.Builder())
                            .build(gcr);
    }

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, PetriNet> getProposerComposerConfiguration(GlobalComponentRepository gcr) {
        return Configurators.<Place, AdvancedComposition<Place>, PetriNet>proposerComposer()
                            .proposer(new ConstrainablePlaceProposer.Builder())
                            .composition(PlaceCollection::new)
                            .terminalComposer(PlaceAccepter::new)
                            .composerChain(PlaceFitnessFilter::new, QueueingPostponingPlaceComposer::new)
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

}
