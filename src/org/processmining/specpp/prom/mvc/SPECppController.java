package org.processmining.specpp.prom.mvc;

import com.google.common.collect.ImmutableList;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.orchestra.SPECppConfigBundle;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.prom.mvc.config.ConfigurationController;
import org.processmining.specpp.prom.mvc.discovery.DiscoveryController;
import org.processmining.specpp.prom.mvc.preprocessing.PreProcessingController;
import org.processmining.specpp.prom.mvc.result.ResultController;
import org.processmining.specpp.prom.plugins.SPECppSession;
import org.processmining.specpp.prom.util.Destructible;
import org.processmining.specpp.util.Reflection;

import javax.swing.*;
import java.util.List;
import java.util.Set;

public class SPECppController {

    public static final ImmutableList<PluginStage> PLUGIN_STAGES = ImmutableList.copyOf(PluginStage.values());
    private final UIPluginContext context;
    private final XLog rawLog;

    private int currentPluginStageIndex;
    private JPanel currentStagePanel;

    private InputDataBundle dataBundle;
    private StageController currentStageController;
    private SPECppPanel myPanel;
    private SPECppConfigBundle configBundle;
    private ProMPetrinetWrapper result;
    private List<Result> intermediatePostProcessingResults;
    private PreProcessingParameters preProcessingParameters;
    private Pair<Set<Activity>> activitySelection;

    public SPECppController(UIPluginContext context, SPECppSession specppSession) {
        this.context = context;
        rawLog = specppSession.getEventLog();
        currentPluginStageIndex = 0;
    }

    public ProMPetrinetWrapper getResult() {
        return result;
    }

    public List<Result> getIntermediatePostProcessingResults() {
        return intermediatePostProcessingResults;
    }



    public enum PluginStage {
        PreProcessing("Pre Processing", PreProcessingController.class), Configuration("Configuration", ConfigurationController.class), Discovery("Discovery", DiscoveryController.class), Results("Results", ResultController.class);

        private final String label;
        private final Class<? extends StageController> controllerClass;


        PluginStage(String label, Class<? extends StageController> controllerClass) {
            this.label = label;
            this.controllerClass = controllerClass;
        }

        public StageController createController(SPECppController parentController) {
            return Reflection.instance(controllerClass, parentController);
        }


        @Override
        public String toString() {
            return label;
        }
    }

    public UIPluginContext getPluginContext() {
        return context;
    }

    public SPECppPanel createPanel() {
        myPanel = new SPECppPanel(this);
        initCurrentPluginStage();
        return myPanel;
    }

    private JPanel createCurrentStagePanel() {
        return currentStageController.createPanel();
    }

    private StageController createCurrentStageController() {
        return currentPluginStage().createController(this);
    }

    private PluginStage currentPluginStage() {
        return PLUGIN_STAGES.get(currentPluginStageIndex);
    }

    private void advanceStage() {
        destroyCurrentPluginStage();
        advanceStageIndex();
        initCurrentPluginStage();
    }

    private void resetStage() {
        destroyCurrentPluginStage();
        initCurrentPluginStage();
    }

    private void destroyCurrentPluginStage() {
        if (currentStagePanel instanceof Destructible) ((Destructible) currentStagePanel).destroy();
        if (currentStageController instanceof Destructible) ((Destructible) currentStageController).destroy();
    }

    private void advanceStageIndex() {
        currentPluginStageIndex++;
    }

    public void preprocessingCompleted(PreProcessingParameters preProcessingParameters, Pair<Set<Activity>> activitySelection, InputDataBundle bundle) {
        this.preProcessingParameters = preProcessingParameters;
        this.activitySelection = activitySelection;
        this.dataBundle = bundle;
        advanceStage();
    }

    public void configCompleted(SPECppConfigBundle configBundle) {
        this.configBundle = configBundle;
        advanceStage();
    }

    public void discoveryCompleted(ProMPetrinetWrapper result, List<Result> intermediatePostProcessingResults) {
        this.result = result;
        this.intermediatePostProcessingResults = intermediatePostProcessingResults;
        myPanel.unlockStage(PluginStage.Results);
    }

    protected void initCurrentPluginStage() {
        currentStageController = createCurrentStageController();
        currentStagePanel = createCurrentStagePanel();
        myPanel.updatePluginStage(currentPluginStage(), currentStagePanel);
    }

    protected void setPluginStage(PluginStage stage) {
        currentPluginStageIndex = PLUGIN_STAGES.indexOf(stage);
        initCurrentPluginStage();
    }

    public XLog getRawLog() {
        return rawLog;
    }

    public InputDataBundle getDataBundle() {
        return dataBundle;
    }

    public PreProcessingParameters getPreProcessingParameters() {
        return preProcessingParameters;
    }

    public Pair<Set<Activity>> getActivitySelection() {
        return activitySelection;
    }

    public SPECppConfigBundle getConfigBundle() {
        return configBundle;
    }
}
