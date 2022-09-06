package org.processmining.specpp.prom.mvc;

import com.google.common.collect.ImmutableList;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
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

    public SPECppController(UIPluginContext context, SPECppSession specppSession) {
        this.context = context;
        rawLog = specppSession.getEventLog();
        currentPluginStageIndex = 0;
    }

    public enum PluginStage {
        PreProcessing(PreProcessingController.class), Config(ConfigurationController.class), Discovery(DiscoveryController.class), Result(ResultController.class);

        private final Class<? extends StageController> controllerClass;


        PluginStage(Class<? extends StageController> controllerClass) {
            this.controllerClass = controllerClass;
        }

        public StageController createController(SPECppController parentController) {
            return Reflection.instance(controllerClass, parentController);
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

    public void preprocessingCompleted(InputDataBundle bundle) {
        dataBundle = bundle;
        advanceStage();
    }

    public void configCompleted(SPECppConfigBundle configBundle) {
        this.configBundle = configBundle;
        advanceStage();
    }

    public void discoveryCompleted(ProMPetrinetWrapper result) {
        this.result = result;
        advanceStage();
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

    public SPECppConfigBundle getConfigBundle() {
        return configBundle;
    }
}
