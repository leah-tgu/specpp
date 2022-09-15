package org.processmining.specpp.prom.mvc.config;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.IdentityPostProcessor;
import org.processmining.specpp.base.impls.*;
import org.processmining.specpp.componenting.data.ParameterRequirements;
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
import org.processmining.specpp.datastructures.tree.heuristic.EventingHeuristicTreeExpansion;
import org.processmining.specpp.datastructures.tree.heuristic.HeuristicTreeExpansion;
import org.processmining.specpp.datastructures.tree.heuristic.TreeNodeScore;
import org.processmining.specpp.datastructures.tree.nodegen.MonotonousPlaceGenerationLogic;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceNode;
import org.processmining.specpp.datastructures.tree.nodegen.PlaceState;
import org.processmining.specpp.evaluation.fitness.AbsolutelyNoFrillsFitnessEvaluator;
import org.processmining.specpp.evaluation.fitness.ForkJoinFitnessEvaluator;
import org.processmining.specpp.evaluation.heuristics.PostponedPlaceScorer;
import org.processmining.specpp.evaluation.markings.LogHistoryMaker;
import org.processmining.specpp.orchestra.AdaptedAlgorithmParameterConfig;
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

    public static ConfiguratorCollection convertToFullConfig(ProMConfig pc) {
        // BUILDING CONFIGURATORS

        // ** SUPERVISION ** //

        boolean logToFile = pc.logToFile;
        SupervisionConfiguration.Configurator svCfg = new SupervisionConfiguration.Configurator();
        svCfg.supervisor(BaseSupervisor::new);
        boolean isSupervisingEvents = pc.supervisionSetting == ConfigurationPanel.SupervisionSetting.Full;
        switch (pc.supervisionSetting) {
            case None:
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
        if (pc.supportRestart) pcCfg.proposer(new RestartablePlaceProposer.Builder());
        else pcCfg.proposer(new ConstrainablePlaceProposer.Builder());
        if (pc.respectWiring || pc.compositionStrategy == ConfigurationPanel.CompositionStrategy.Uniwired)
            pcCfg.composition(ConstrainingPlaceCollection::new);
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
        if (pc.compositionStrategy == ConfigurationPanel.CompositionStrategy.TauDelta)
            evCfg.evaluatorProvider(pc.bridgedDelta.getBridge().getBuilder());
        else if (pc.compositionStrategy == ConfigurationPanel.CompositionStrategy.Uniwired)
            evCfg.evaluatorProvider(new PostponedPlaceScorer.Builder());

        EfficientTreeConfiguration.Configurator<Place, PlaceState, PlaceNode> etCfg;
        if (pc.treeExpansionSetting == ConfigurationPanel.TreeExpansionSetting.Heuristic) {
            HeuristicTreeConfiguration.Configurator<Place, PlaceState, PlaceNode, TreeNodeScore> htCfg = new HeuristicTreeConfiguration.Configurator<>();
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

        PostProcessingConfiguration.Configurator configurator = new PostProcessingConfiguration.Configurator<PetriNet, PetriNet>(IdentityPostProcessor::new);
        for (FrameworkBridge.AnnotatedPostProcessor annotatedPostProcessor : pc.ppPipeline) {
            configurator.processor(annotatedPostProcessor.getBuilder());
        }
        PostProcessingConfiguration.Configurator<PetriNet, ProMPetrinetWrapper> ppCfg = (PostProcessingConfiguration.Configurator<PetriNet, ProMPetrinetWrapper>) configurator;//;.processor(ProMConverter::new);

        // ** PARAMETERS ** //

        ExecutionParameters exp = new ExecutionParameters(new ExecutionParameters.ExecutionTimeLimits(pc.discoveryTimeLimit, null, pc.totalTimeLimit), ExecutionParameters.ParallelizationTarget.Moderate, ExecutionParameters.PerformanceFocus.Balanced);
        PlaceGeneratorParameters pgp = new PlaceGeneratorParameters(pc.depth < 0 ? Integer.MAX_VALUE : pc.depth, true, pc.respectWiring, false, false);


        class CustomParameters extends AbstractGlobalComponentSystemUser implements ProvidesParameters {
            public CustomParameters() {
                globalComponentSystem().provide(ParameterRequirements.EXECUTION_PARAMETERS.fulfilWithStatic(exp))
                                       .provide(ParameterRequirements.SUPERVISION_PARAMETERS.fulfilWithStatic(pc.supervisionSetting != ConfigurationPanel.SupervisionSetting.None ? SupervisionParameters.instrumentAll(false, logToFile) : SupervisionParameters.instrumentNone(false, logToFile)))
                                       .provide(ParameterRequirements.TAU_FITNESS_THRESHOLDS.fulfilWithStatic(TauFitnessThresholds.tau(pc.tau)))
                                       .provide(ParameterRequirements.PLACE_GENERATOR_PARAMETERS.fulfilWithStatic(pgp))
                                       .provide(ParameterRequirements.OUTPUT_PATH_PARAMETERS.fulfilWithStatic(OutputPathParameters.getDefault()));
                if (pc.compositionStrategy == ConfigurationPanel.CompositionStrategy.TauDelta)
                    globalComponentSystem().provide(ParameterRequirements.DELTA_PARAMETERS.fulfilWithStatic(new DeltaParameters(pc.delta, pc.steepness)));
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

    @Override
    public void startup() {

    }

    public void basicConfigCompleted(ProMConfig basicConfig) {
        ConfiguratorCollection fullConfig = convertToFullConfig(basicConfig);
        parentController.configCompleted(basicConfig, fullConfig);
    }

}
