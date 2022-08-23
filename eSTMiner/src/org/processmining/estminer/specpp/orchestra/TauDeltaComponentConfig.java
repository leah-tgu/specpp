package org.processmining.estminer.specpp.orchestra;

import org.processmining.estminer.specpp.base.AdvancedComposition;
import org.processmining.estminer.specpp.base.Composer;
import org.processmining.estminer.specpp.base.impls.AcceptingComposer;
import org.processmining.estminer.specpp.base.impls.PlaceFitnessFilter;
import org.processmining.estminer.specpp.base.impls.QueueingPostponingPlaceComposer;
import org.processmining.estminer.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.estminer.specpp.componenting.system.ComponentCollection;
import org.processmining.estminer.specpp.composition.PlaceCollection;
import org.processmining.estminer.specpp.config.Configurators;
import org.processmining.estminer.specpp.config.EfficientTreeConfiguration;
import org.processmining.estminer.specpp.config.ProposerComposerConfiguration;
import org.processmining.estminer.specpp.datastructures.petri.PetriNet;
import org.processmining.estminer.specpp.datastructures.petri.Place;
import org.processmining.estminer.specpp.datastructures.tree.base.PlaceGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.base.impls.EventingEnumeratingTree;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.EventingHeuristicTreeExpansion;
import org.processmining.estminer.specpp.datastructures.tree.heuristic.HeuristicUtils;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.estminer.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.estminer.specpp.evaluation.fitness.AbsolutelyNoFrillsFitnessEvaluator;
import org.processmining.estminer.specpp.evaluation.heuristics.DeltaAdaptationFunction;
import org.processmining.estminer.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.estminer.specpp.proposal.ConstrainablePlaceProposer;

public class TauDeltaComponentConfig extends BaseSpecOpsComponentConfig {

    @Override
    public EvaluatorConfiguration getEvaluatorConfiguration(ComponentCollection csa) {
        return Configurators.evaluators()
                            .evaluatorProvider(DeltaAdaptationFunction.Provider::new)
                            .evaluatorProvider(LogHistoryMaker::new)
                            .evaluatorProvider(AbsolutelyNoFrillsFitnessEvaluator::new)
                            .build(csa);
    }

    @Override
    public ProposerComposerConfiguration<Place, AdvancedComposition<Place>, PetriNet> getProposerComposerConfiguration(ComponentCollection csa) {
        return Configurators.<Place, AdvancedComposition<Place>, PetriNet>proposerComposer()
                            .proposer(new ConstrainablePlaceProposer.Builder())
                            .composition(PlaceCollection::new)
                            .composerChain(comp -> new PlaceFitnessFilter<>((Composer<Place, PlaceCollection, PetriNet>) comp), comp -> new QueueingPostponingPlaceComposer<>((Composer<Place, PlaceCollection, PetriNet>) comp), (Object coll) -> new AcceptingComposer<>((PlaceCollection) coll, cc -> new PetriNet(cc.toSet())))
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

}
