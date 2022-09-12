package org.processmining.specpp.prom.mvc.result;

import com.fluxicon.slickerbox.components.SlickerTabbedPane;
import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.prom.mvc.error.MessagePanel;
import org.processmining.specpp.prom.mvc.swing.TitledBorderPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResultPanel extends AbstractStagePanel<ResultController> {

    private final List<String> tabTitles;

    public ResultPanel(ResultController controller, ProMPetrinetWrapper proMPetrinetWrapper, List<Result> intermediatePostProcessingResults) {
        super(controller, new GridBagLayout());


        List<PetriNet> intermediatePetriNets = intermediatePostProcessingResults.stream()
                                                                                .filter(r -> r instanceof PetriNet)
                                                                                .map(r -> (PetriNet) r)
                                                                                .collect(Collectors.toList());
        List<ProMPetrinetWrapper> intermediateProMPetriNets = intermediatePostProcessingResults.stream()
                                                                                               .filter(r -> r instanceof ProMPetrinetWrapper)
                                                                                               .map(r -> (ProMPetrinetWrapper) r)
                                                                                               .collect(Collectors.toList());


        SlickerTabbedPane tabbedPane = SlickerFactory.instance().createTabbedPane("Results");

        tabTitles = new ArrayList<>();
        int i = 1;
        int size = intermediatePostProcessingResults.size();
        for (Result result : intermediatePostProcessingResults) {
            JComponent comp;
            if (result instanceof PetriNet) {
                comp = new PetriNetResultPanel(((PetriNet) result), controller.getFitnessEvaluator(), controller.getVariantFrequencies());
            } else if (result instanceof ProMPetrinetWrapper) {
                comp = new ProMPetrinetResultPanel((ProMPetrinetWrapper) result);
            } else {
                comp = new MessagePanel(Objects.toString(result));
            }
            String s = "PostProcessing Step " + i;
            if (i == size) s += " (final result)";
            tabbedPane.addTab(s, comp);
            tabTitles.add(s);
            i++;
        }

        TitledBorderPanel bottomPanel = new TitledBorderPanel("Final Result Info", new BorderLayout());
        ResultEvaluationPanel resultEvaluationPanel = new ResultEvaluationPanel(controller, proMPetrinetWrapper);
        bottomPanel.add(resultEvaluationPanel);

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        add(tabbedPane, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0;
        c.gridy++;
        add(bottomPanel, c);

        String last = tabTitles.get(tabTitles.size() - 1);
        tabbedPane.selectTab(last);
    }


}
