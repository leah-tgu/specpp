package org.processmining.specpp.prom.mvc.config;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.IdentityPostProcessor;
import org.processmining.specpp.base.impls.*;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.data.StaticDataSource;
import org.processmining.specpp.componenting.evaluation.EvaluatorConfiguration;
import org.processmining.specpp.componenting.system.AbstractGlobalComponentSystemUser;
import org.processmining.specpp.componenting.traits.ProvidesParameters;
import org.processmining.specpp.composition.ConstrainingPlaceCollection;
import org.processmining.specpp.composition.PlaceCollection;
import org.processmining.specpp.config.*;
import org.processmining.specpp.config.parameters.*;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.tree.base.impls.EnumeratingTree;
import org.processmining.specpp.datastructures.tree.base.impls.EventingEnumeratingTree;
import org.processmining.specpp.datastructures.tree.base.impls.VariableExpansion;
import org.processmining.specpp.datastructures.tree.heuristic.DoubleScore;
import org.processmining.specpp.datastructures.tree.heuristic.EventingHeuristicTreeExpansion;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicTreeExpansion;
import org.processmining.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.evaluation.fitness.AbsolutelyNoFrillsFitnessEvaluator;
import org.processmining.specpp.evaluation.fitness.ForkJoinFitnessEvaluator;
import org.processmining.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.specpp.orchestra.AdaptedAlgorithmParameterConfig;
import org.processmining.specpp.postprocessing.ProMConverter;
import org.processmining.specpp.prom.alg.FrameworkBridge;
import org.processmining.specpp.prom.alg.LiveEvents;
import org.processmining.specpp.prom.alg.LivePerformance;
import org.processmining.specpp.prom.mvc.AbstractStageController;
import org.processmining.specpp.prom.mvc.SPECppController;
import org.processmining.specpp.proposal.ConstrainablePlaceProposer;
import org.processmining.specpp.proposal.RestartablePlaceProposer;
import org.processmining.specpp.supervision.supervisors.BaseSupervisor;
import org.processmining.specpp.supervision.supervisors.TerminalSupervisor;

import javax.swing.*;

public class ConfigurationController extends AbstractStageController {


    public ConfigurationController(SPECppController parentController) {
        super(parentController);
    }

    public static ConfiguratorCollection convertToFullConfig(ConfigurationPanel.ProMConfig pc) {
        // BUILDING CONFIGURATORS

        // ** SUPERVISION ** //

        SupervisionConfiguration.Configurator svCfg = new SupervisionConfiguration.Configurator();
        svCfg.supervisor(BaseSupervisor::new);
        boolean isSupervisingEvents = pc.supervisionSetting == ConfigurationPanel.SupervisionSetting.Full;
        switch (pc.supervisionSetting) {
            case Lightweight:
                break;
            case PerformanceOnly:
                svCfg.supervisor(LivePerformance::new);
                break;
            case Full:
                svCfg.supervisor(LivePerformance::new);
                svCfg.supervisor(LiveEvents::new);
                break;
        }
        svCfg.supervisor(TerminalSupervisor::new);

        // ** PROPOSAL, COMPOSITION ** //

        ProposerComposerConfiguration.Configurator<Place, AdvancedComposition<Place>, PetriNet> pcCfg = new ProposerComposerConfiguration.Configurator<>();
        if (pc.permitRestart) pcCfg.proposer(new RestartablePlaceProposer.Builder());
        else pcCfg.proposer(new ConstrainablePlaceProposer.Builder());
        if (pc.permitWiring) pcCfg.composition(ConstrainingPlaceCollection::new);
        else if (pc.compositionStrategy == ConfigurationPanel.CompositionStrategy.TauDelta || pc.applyCIPR)
            pcCfg.composition(PlaceCollection::new);
        else pcCfg.composition(LightweightPlaceCollection::new);
        if (pc.applyCIPR)
            pcCfg.terminalComposer(isSupervisingEvents ? EventingPlaceComposerWithCIPR::new : PlaceComposerWithCIPR::new);
        else pcCfg.terminalComposer(PlaceAccepter::new);
        switch (pc.compositionStrategy) {
            case Standard:
                pcCfg.composerChain(isSupervisingEvents ? EventingPlaceFitnessFilter::new : PlaceFitnessFilter::new);
                break;
            case TauDelta:
                pcCfg.composerChain(isSupervisingEvents ? EventingPlaceFitnessFilter::new : PlaceFitnessFilter::new, QueueingDeltaComposer::new);
                break;
            case Uniwired:
                pcCfg.composerChain(isSupervisingEvents ? EventingPlaceFitnessFilter::new : PlaceFitnessFilter::new, UniwiredComposer::new);
                break;
        }

        // ** EVALUATION ** //

        EvaluatorConfiguration.Configurator evCfg = new EvaluatorConfiguration.Configurator();
        evCfg.evaluatorProvider(LogHistoryMaker::new);
        evCfg.evaluatorProvider(pc.concurrentReplay ? ForkJoinFitnessEvaluator::new : AbsolutelyNoFrillsFitnessEvaluator::new);
        if (pc.compositionStrategy == ConfigurationPanel.CompositionStrategy.TauDelta) evCfg.evaluatorProvider(pc.bridgedDelta.getBridge().getBuilder());

        EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> etCfg;
        if (pc.treeExpansionSetting == ConfigurationPanel.TreeExpansionSetting.Heuristic) {
            HeuristicTreeConfiguration.Configurator<Place, PlaceState, PlaceNode, DoubleScore> htCfg = new HeuristicTreeConfiguration.Configurator<>();
            htCfg.heuristic(pc.bridgedHeuristics.getBridge().getBuilder());
            htCfg.heuristicExpansion(isSupervisingEvents ? EventingHeuristicTreeExpansion::new : HeuristicTreeExpansion::new);
            htCfg.tree(isSupervisingEvents ? EventingEnumeratingTree::new : EnumeratingTree::new);
            htCfg.childGenerationLogic(new MonotonousPlaceGenerationLogic.Builder());
            etCfg = htCfg;
        } else {
            etCfg = new EfficientTreeConfiguration.Configurator<>();
            etCfg.tree(isSupervisingEvents ? EventingEnumeratingTree::new : EnumeratingTree::new);
            etCfg.expansionStrategy(pc.treeExpansionSetting == ConfigurationPanel.TreeExpansionSetting.BFS ? VariableExpansion::bfs : VariableExpansion::dfs);
            etCfg.childGenerationLogic(new MonotonousPlaceGenerationLogic.Builder());
        }

        // ** Post Processing ** //

        PostProcessingConfiguration.Configurator<PetriNet, PetriNet> configurator = new PostProcessingConfiguration.Configurator<>(IdentityPostProcessor::new);
        for (FrameworkBridge.BridgedPostProcessors bridgedPostProcessors : pc.ppPipeline) {
            FrameworkBridge.BridgedPostProcessor next = bridgedPostProcessors.getBridge();
            configurator.processor((SimpleBuilder) next.getBuilder());
        }
        PostProcessingConfiguration.Configurator<PetriNet, ProMPetrinetWrapper> ppCfg = configurator.processor(ProMConverter::new);

        // ** PARAMETERS ** //

        ExecutionParameters exp = new ExecutionParameters(new ExecutionParameters.ExecutionTimeLimits(pc.discoveryTimeLimit, null, pc.totalTimeLimit), ExecutionParameters.ParallelizationTarget.Moderate, ExecutionParameters.PerformanceFocus.Balanced);
        PlaceGeneratorParameters pgp = new PlaceGeneratorParameters(pc.depth < 0 ? Integer.MAX_VALUE : pc.depth, true, pc.permitWiring, false, false);

        class CustomParameters extends AbstractGlobalComponentSystemUser implements ProvidesParameters {
            public CustomParameters() {
                globalComponentSystem().provide(ParameterRequirements.EXECUTION_PARAMETERS.fulfilWith(StaticDataSource.of(exp)))
                                       .provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWith(StaticDataSource.of(pc.supervisionSetting != ConfigurationPanel.SupervisionSetting.Lightweight ? SupervisionParameters.instrumentAll(false) : SupervisionParameters.instrumentNone(false))))
                                       .provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWith(StaticDataSource.of(TauFitnessThresholds.tau(pc.tau))))
                                       .provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWith(StaticDataSource.of(pgp)));
                if (pc.compositionStrategy == ConfigurationPanel.CompositionStrategy.TauDelta)
                    globalComponentSystem().provide(ParameterRequirements.DELTA_PARAMETERS.fulfilWith(StaticDataSource.of(new DeltaParameters(pc.delta))));
            }
        }

        ProvidesParameters parameters = new CustomParameters();
        AdaptedAlgorithmParameterConfig parCfg = new AdaptedAlgorithmParameterConfig(parameters);

        return new ConfiguratorCollection(svCfg, pcCfg, evCfg, etCfg, ppCfg, parCfg);
    }

    @Override
    public JPanel createPanel() {
        return new ConfigurationPanel(this);
    }

    public void basicConfigCompleted(ConfigurationPanel.ProMConfig basicConfig) {
        ConfiguratorCollection fullConfig = convertToFullConfig(basicConfig);
        parentController.configCompleted(fullConfig);
    }

}
