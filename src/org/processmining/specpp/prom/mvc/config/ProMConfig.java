package org.processmining.specpp.prom.mvc.config;

import com.google.common.collect.ImmutableList;
import org.processmining.specpp.datastructures.vectorization.OrderingRelation;
import org.processmining.specpp.evaluation.implicitness.ImplicitnessTestingParameters;
import org.processmining.specpp.prom.alg.FrameworkBridge;

import java.time.Duration;
import java.util.List;

public class ProMConfig {
    SupervisionSetting supervisionSetting;
    boolean logToFile;
    TreeExpansionSetting treeExpansionSetting;
    boolean respectWiring, supportRestart;
    FrameworkBridge.AnnotatedTreeHeuristic treeHeuristic;
    boolean concurrentReplay, permitNegativeMarkingsDuringReplay;
    ImplicitnessTestingParameters.SubLogRestriction implicitnessReplaySubLogRestriction;
    FrameworkBridge.AnnotatedEvaluator deltaAdaptationFunction;
    public boolean enforceHeuristicThreshold;
    public double heuristicThreshold;
    public OrderingRelation heuristicThresholdRelation;
    CompositionStrategy compositionStrategy;
    CIPRVariant ciprVariant;
    List<FrameworkBridge.AnnotatedPostProcessor> ppPipeline;
    double tau, delta;
    public int steepness;
    int depth;
    Duration discoveryTimeLimit, totalTimeLimit;

    public ProMConfig() {
    }

    public static ProMConfig getDefault() {
        ProMConfig pc = new ProMConfig();
        pc.supervisionSetting = SupervisionSetting.Full;
        pc.logToFile = true;
        pc.treeExpansionSetting = TreeExpansionSetting.Heuristic;
        pc.respectWiring = false;
        pc.supportRestart = false;
        pc.treeHeuristic = FrameworkBridge.BridgedHeuristics.BFS_Emulation.getBridge();
        pc.enforceHeuristicThreshold = false;
        pc.concurrentReplay = false;
        pc.permitNegativeMarkingsDuringReplay = false;
        pc.implicitnessReplaySubLogRestriction = ImplicitnessTestingParameters.SubLogRestriction.None;
        pc.deltaAdaptationFunction = FrameworkBridge.BridgedDeltaAdaptationFunctions.Constant.getBridge();
        pc.compositionStrategy = CompositionStrategy.Standard;
        pc.ciprVariant = CIPRVariant.ReplayBased;
        pc.ppPipeline = ImmutableList.of(FrameworkBridge.BridgedPostProcessors.LPBasedImplicitPlaceRemoval.getBridge(), FrameworkBridge.BridgedPostProcessors.ProMPetrinetConversion.getBridge());
        pc.tau = 1.0;
        pc.delta = -1.0;
        pc.steepness = -1;
        pc.heuristicThreshold = -1;
        pc.depth = -1;
        pc.discoveryTimeLimit = null;
        pc.totalTimeLimit = null;
        return pc;
    }

    public static ProMConfig getLightweight() {
        ProMConfig pc = getDefault();
        pc.supervisionSetting = SupervisionSetting.None;
        pc.treeExpansionSetting = TreeExpansionSetting.DFS;
        return pc;
    }

    public boolean validate() {
        boolean outOfRange = tau < 0 || tau > 1.0;
        outOfRange |= compositionStrategy == CompositionStrategy.TauDelta && delta < 0;
        boolean incomplete = (supervisionSetting == null | treeExpansionSetting == null | compositionStrategy == null);
        incomplete |= treeExpansionSetting == TreeExpansionSetting.Heuristic && treeHeuristic == null;
        incomplete |= enforceHeuristicThreshold && (heuristicThreshold < 0 || heuristicThresholdRelation == null);
        incomplete |= compositionStrategy == CompositionStrategy.TauDelta && (deltaAdaptationFunction == null || delta < 0 || ((deltaAdaptationFunction == FrameworkBridge.BridgedDeltaAdaptationFunctions.Linear.getBridge() || deltaAdaptationFunction == FrameworkBridge.BridgedDeltaAdaptationFunctions.Sigmoid.getBridge()) && steepness < 0));
        return !outOfRange && !incomplete;
    }

    public enum SupervisionSetting {
        None, PerformanceOnly, Full
    }

    public enum TreeExpansionSetting {
        BFS, DFS, Heuristic
    }

    public enum CompositionStrategy {
        Standard("Standard", ""), TauDelta("Tau-Delta", ""), Uniwired("Uniwired", "");

        private final String printableName;
        private final String description;

        CompositionStrategy(String printableName, String description) {
            this.printableName = printableName;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return printableName;
        }
    }

    public enum CIPRVariant {
        None(ImplicitnessTestingParameters.CIPRVersion.None), ReplayBased(ImplicitnessTestingParameters.CIPRVersion.ReplayBased), LPBased(ImplicitnessTestingParameters.CIPRVersion.LPBased);

        private final ImplicitnessTestingParameters.CIPRVersion bridge;

        CIPRVariant(ImplicitnessTestingParameters.CIPRVersion bridge) {
            this.bridge = bridge;
        }

        public ImplicitnessTestingParameters.CIPRVersion bridge() {
            return bridge;
        }
    }

}
