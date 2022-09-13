package org.processmining.specpp.prom.mvc.result;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.prom.mvc.config.ProMConfig;
import org.processmining.specpp.prom.plugins.ProMSPECppConfig;

import javax.swing.*;

public class ResultExportPanel extends AbstractStagePanel<ResultController> {

    private final PluginContext context;

    public ResultExportPanel(ResultController controller) {
        super(controller);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JButton saveProMPetriButton = SlickerFactory.instance().createButton("save Petri net to workspace");
        saveProMPetriButton.addActionListener(e -> saveProMPetri());
        add(saveProMPetriButton);
        JButton saveConfigButton = SlickerFactory.instance().createButton("save config to workspace");
        saveProMPetriButton.addActionListener(e -> saveConfig());
        add(saveConfigButton);
        context = this.controller.getContext();
    }

    private void saveProMPetri() {
        // TODO figure out how to favorite these
        context.getProvidedObjectManager()
               .createProvidedObject("Petrinet", controller.getResult(), AcceptingPetriNet.class, context);
    }

    private void saveConfig() {
        ProMConfig proMConfig = controller.getParentController().getProMConfig();
        PreProcessingParameters preProcessingParameters = controller.getParentController().getPreProcessingParameters();
        context.getProvidedObjectManager()
               .createProvidedObject("Config", new ProMSPECppConfig(preProcessingParameters, proMConfig), ProMSPECppConfig.class, context);
    }


}
