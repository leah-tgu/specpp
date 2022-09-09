package org.processmining.specpp.prom.mvc.result;

import com.fluxicon.slickerbox.components.SlickerTabbedPane;
import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.graphvisualizers.algorithms.GraphVisualizerAlgorithm;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.prom.mvc.discovery.LivePlacesGraph;
import org.processmining.specpp.prom.mvc.discovery.LivePlacesList;
import org.processmining.specpp.prom.mvc.error.MessagePanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ResultPanel extends AbstractStagePanel<ResultController> {

    private final JPanel graphPanel;
    private final JPanel listPanel;
    private final ProMPetrinetWrapper proMPetrinetWrapper;
    private final PetriNet petriNet;

    public ResultPanel(ResultController controller, ProMPetrinetWrapper proMPetrinetWrapper, java.util.List<Result> intermediatePostProcessingResults) {
        super(controller, new GridBagLayout());
        this.proMPetrinetWrapper = proMPetrinetWrapper;
        java.util.List<PetriNet> intermediatePetriNets = intermediatePostProcessingResults.stream()
                                                                                          .filter(r -> r instanceof PetriNet)
                                                                                          .map(r -> (PetriNet) r)
                                                                                          .collect(Collectors.toList());
        petriNet = intermediatePetriNets.get(intermediatePetriNets.size() - 1);


        SlickerTabbedPane tabbedPane = SlickerFactory.instance().createTabbedPane("Result");
        ResultEvaluationPanel resultEvaluationPanel = new ResultEvaluationPanel(proMPetrinetWrapper, petriNet);

        graphPanel = new JPanel();
        listPanel = new JPanel();

        tabbedPane.addTab("Graph", graphPanel);
        tabbedPane.addTab("List", listPanel);

        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        add(tabbedPane, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0;
        add(resultEvaluationPanel, c);

       startVisualizationWorkers();
       tabbedPane.selectTab("List");
    }

    private void startVisualizationWorkers() {
        new SwingWorker<JComponent, Void>() {

            @Override
            protected JComponent doInBackground() throws Exception {
                LivePlacesGraph lg = new LivePlacesGraph();
                lg.update(proMPetrinetWrapper);
                return lg.getComponent();
            }

            @Override
            protected void done() {
                try {
                    JComponent jComponent = get();
                    graphPanel.add(jComponent);
                    jComponent.revalidate();
                } catch (ExecutionException | InterruptedException e) {
                    graphPanel.add(new MessagePanel("Graph Visualizer failed.\n" + e.getMessage()));
                    graphPanel.revalidate();
                }
            }
        }.execute();

        new SwingWorker<JComponent, Void>() {

            @Override
            protected JComponent doInBackground() throws Exception {
                LivePlacesList ll = new LivePlacesList();
                ll.update(new ArrayList<>(petriNet.getPlaces()));
                return ll.getComponent();
            }

            @Override
            protected void done() {
                try {
                    JComponent jComponent = get();
                    listPanel.add(jComponent);
                    jComponent.revalidate();
                } catch (ExecutionException | InterruptedException e) {
                    listPanel.add(new MessagePanel("List Visualizer failed.\n" + e.getMessage()));
                    listPanel.revalidate();
                }
            }
        }.execute();
    }


}
