package org.processmining.specpp.prom.mvc.config;

import com.google.common.collect.ImmutableList;
import org.processmining.specpp.prom.alg.FrameworkBridge;

import java.time.Duration;
import java.util.List;

public class ProMConfig {
    ConfigurationPanel.SupervisionSetting supervisionSetting;
    boolean logToFile;
    ConfigurationPanel.TreeExpansionSetting treeExpansionSetting;
    boolean respectWiring, supportRestart;
    FrameworkBridge.BridgedHeuristics bridgedHeuristics;
    boolean concurrentReplay, restrictToFittingSubLog;
    FrameworkBridge.BridgedDeltaAdaptationFunctions bridgedDelta;
    ConfigurationPanel.CompositionStrategy compositionStrategy;
    boolean applyCIPR;
    List<FrameworkBridge.AnnotatedPostProcessor> ppPipeline;
    double tau, delta;
    public int steepness;
    int depth;
    Duration discoveryTimeLimit, totalTimeLimit;

    public ProMConfig() {
    }

    public static ProMConfig getDefault() {
        ProMConfig pc = new ProMConfig();
        pc.supervisionSetting = ConfigurationPanel.SupervisionSetting.Full;
        pc.logToFile = true;
        pc.treeExpansionSetting = ConfigurationPanel.TreeExpansionSetting.Heuristic;
        pc.respectWiring = false;
        pc.supportRestart = false;
        pc.bridgedHeuristics = FrameworkBridge.BridgedHeuristics.BFS_Emulation;
        pc.concurrentReplay = false;
        pc.restrictToFittingSubLog = false;
        pc.bridgedDelta = FrameworkBridge.BridgedDeltaAdaptationFunctions.Constant;
        pc.compositionStrategy = ConfigurationPanel.CompositionStrategy.Standard;
        pc.applyCIPR = true;
        pc.ppPipeline = ImmutableList.of(FrameworkBridge.BridgedPostProcessors.ReplayBasedImplicitPlaceRemoval.getBridge(), FrameworkBridge.BridgedPostProcessors.SelfLoopPlacesMerging.getBridge(), FrameworkBridge.BridgedPostProcessors.ProMPetrinetConversion.getBridge());
        pc.tau = 1.0;
        pc.delta = -1.0;
        pc.steepness = -1;
        pc.depth = -1;
        pc.discoveryTimeLimit = null;
        pc.totalTimeLimit = null;
        return pc;
    }

    public static ProMConfig getLightweight() {
        ProMConfig pc = getDefault();
        pc.supervisionSetting = ConfigurationPanel.SupervisionSetting.None;
        pc.treeExpansionSetting = ConfigurationPanel.TreeExpansionSetting.DFS;
        return pc;
    }

    public boolean validate() {
        boolean outOfRange = tau < 0 || tau > 1.0;
        outOfRange |= compositionStrategy == ConfigurationPanel.CompositionStrategy.TauDelta && delta < 0;
        boolean incomplete = (supervisionSetting == null | treeExpansionSetting == null | compositionStrategy == null);
        incomplete |= treeExpansionSetting == ConfigurationPanel.TreeExpansionSetting.Heuristic && bridgedHeuristics == null;
        incomplete |= compositionStrategy == ConfigurationPanel.CompositionStrategy.TauDelta && (bridgedDelta == null || delta < 0 || ((bridgedDelta == FrameworkBridge.BridgedDeltaAdaptationFunctions.Linear || bridgedDelta == FrameworkBridge.BridgedDeltaAdaptationFunctions.Sigmoid) && steepness < 0));
        return !outOfRange && !incomplete;
    }

}
