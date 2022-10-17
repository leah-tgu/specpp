package org.processmining.specpp.prom.mvc.result;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.utils.ProvidedObjectHelper;
import org.processmining.specpp.config.parameters.OutputPathParameters;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.postprocessing.PlaceExporter;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.prom.mvc.config.ProMConfig;
import org.processmining.specpp.prom.plugins.ProMSPECppConfig;

import javax.swing.*;

public class ResultExportPanel extends AbstractStagePanel<ResultController> {

    private final PluginContext context;
    private final JButton saveProMPetriButton;
    private final JButton saveConfigButton;

    public ResultExportPanel(ResultController controller) {
        super(controller);
        context = this.controller.getContext();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        saveProMPetriButton = SlickerFactory.instance().createButton("save Petri net to workspace");
        saveProMPetriButton.addActionListener(e -> saveProMPetri());
        add(saveProMPetriButton);
        saveConfigButton = SlickerFactory.instance().createButton("save config to workspace");
        saveConfigButton.addActionListener(e -> saveConfig());
        add(saveConfigButton);
    }

    private void saveProMPetri() {
        ProMPetrinetWrapper result = controller.getResult();
        context.getProvidedObjectManager()
               .createProvidedObject("Petrinet", result, AcceptingPetriNet.class, context);
        ProvidedObjectHelper.setFavorite(context, result);
        saveProMPetriButton.setEnabled(false);
    }

    private void saveConfig() {
        ProMConfig proMConfig = controller.getParentController().getProMConfig();
        PreProcessingParameters preProcessingParameters = controller.getParentController().getPreProcessingParameters();
        ProMSPECppConfig config = new ProMSPECppConfig(preProcessingParameters, proMConfig);
        context.getProvidedObjectManager()
               .createProvidedObject("Config", config, ProMSPECppConfig.class, context);
        ProvidedObjectHelper.setFavorite(context, config);
        saveConfigButton.setEnabled(false);
    }


}
