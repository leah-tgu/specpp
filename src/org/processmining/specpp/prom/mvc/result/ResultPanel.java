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

public class ResultPanel extends AbstractStagePanel<ResultController> {

    private final List<String> tabTitles;

    public ResultPanel(ResultController controller, ProMPetrinetWrapper result, List<Result> intermediatePostProcessingResults) {
        super(controller, new GridBagLayout());

        SlickerTabbedPane tabbedPane = SlickerFactory.instance().createTabbedPane("Results");

        tabTitles = new ArrayList<>();
        int i = 1;
        int size = intermediatePostProcessingResults.size();
        for (Result r : intermediatePostProcessingResults) {
            JComponent comp;
            if (r instanceof PetriNet) {
                comp = new PetriNetResultPanel(((PetriNet) r), controller.getFitnessEvaluator(), controller.getVariantFrequencies());
            } else if (r instanceof ProMPetrinetWrapper) {
                comp = new ProMPetrinetResultPanel((ProMPetrinetWrapper) r);
            } else {
                comp = new MessagePanel(Objects.toString(r));
            }
            String s = "Post Processing Step " + i;
            if (i == size) s += " (final result)";
            tabbedPane.addTab(s, comp);
            tabTitles.add(s);
            i++;
        }

        TitledBorderPanel infoPanel = new TitledBorderPanel("Final Result Info");
        ResultEvaluationPanel resultEvaluationPanel = new ResultEvaluationPanel(controller, result);
        infoPanel.append(resultEvaluationPanel);
        infoPanel.completeWithWhitespace();

        TitledBorderPanel exportPanel = new TitledBorderPanel("Export Panel");
        ResultExportPanel resultExportPanel = new ResultExportPanel(controller);
        exportPanel.append(resultExportPanel);
        exportPanel.completeWithWhitespace();

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 2;
        add(tabbedPane, c);
        c.gridwidth = 1;
        //c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0;
        c.gridy++;
        add(infoPanel, c);
        c.gridx++;
        add(exportPanel, c);

        String last = tabTitles.get(tabTitles.size() - 1);
        tabbedPane.selectTab(last);
    }


}
