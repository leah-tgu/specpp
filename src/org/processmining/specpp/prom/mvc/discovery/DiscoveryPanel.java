package org.processmining.specpp.prom.mvc.discovery;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.alg.LiveEvents;
import org.processmining.specpp.prom.alg.LivePerformance;
import org.processmining.specpp.prom.computations.OngoingComputation;
import org.processmining.specpp.prom.computations.OngoingStagedComputation;
import org.processmining.specpp.prom.computations.StagedComputationListeningPanel;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.prom.mvc.swing.TitledBorderPanel;

import javax.swing.*;
import java.awt.*;

public class DiscoveryPanel extends AbstractStagePanel<DiscoveryController> {

    private final SPECpp<Place, AdvancedComposition<Place>, PetriNet, ProMPetrinetWrapper> specpp;

    public DiscoveryPanel(DiscoveryController discoveryController) {
        super(discoveryController, new BorderLayout());
        specpp = discoveryController.getSpecpp();

        TitledBorderPanel executionPanel = new TitledBorderPanel("Execution");
        ComputationListeningPanel<OngoingComputation> discoveryListeningPanel = new ComputationListeningPanel<>("Discovery", discoveryController.getOngoingDiscoveryComputation());
        ComputationListeningPanel<OngoingStagedComputation> postProcessingListeningPanel = new StagedComputationListeningPanel<>("Post Processing", discoveryController.getOngoingPostProcessingComputation());

        executionPanel.append(discoveryListeningPanel);
        executionPanel.append(postProcessingListeningPanel);
        executionPanel.completeWithWhitespace();

        LiveCompositionPanel liveCompositionPanel = new LiveCompositionPanel(specpp.getComposer()
                                                                                   .getIntermediateResult(), discoveryController.getOngoingDiscoveryComputation());

        TitledBorderPanel searchSpacePanel = new TitledBorderPanel("Search Space", new BorderLayout());
        searchSpacePanel.add(new SearchSpacePanel(specpp), BorderLayout.CENTER);

        TitledBorderPanel performancePanel = new TitledBorderPanel("Performance", new BorderLayout());
        LivePerformance livePerf = specpp.getSupervisors()
                                         .stream()
                                         .filter(s -> s instanceof LivePerformance)
                                         .map(s -> (LivePerformance) s)
                                         .findFirst()
                                         .orElse(null);
        performancePanel.add(new PerformanceTable(livePerf), BorderLayout.CENTER);

        TitledBorderPanel eventsPanel = new TitledBorderPanel("Events", new BorderLayout());
        LiveEvents liveEvents = specpp.getSupervisors()
                                      .stream()
                                      .filter(s -> s instanceof LiveEvents)
                                      .map(s -> (LiveEvents) s)
                                      .findFirst()
                                      .orElse(null);
        eventsPanel.add(new EventTable(liveEvents), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        //liveCompositionPanel.setMinimumSize(new Dimension(600, 400));
        splitPane.setLeftComponent(liveCompositionPanel);
        JPanel right = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 0;
        c.gridx = 0;
        c.weightx = 1;
        right.add(searchSpacePanel, c);
        c.gridy++;
        right.add(executionPanel, c);
        c.weighty = 1;
        c.gridy++;
        right.add(performancePanel, c);
        c.gridy++;
        right.add(eventsPanel, c);
        c.gridy++;
        c.weighty = 0;
        right.add(Box.createHorizontalStrut(300), c);

        splitPane.setRightComponent(right);
        add(splitPane, BorderLayout.CENTER);
    }


}
