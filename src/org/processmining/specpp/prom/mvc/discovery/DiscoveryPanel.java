package org.processmining.specpp.prom.mvc.discovery;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.alg.LiveEvents;
import org.processmining.specpp.prom.alg.LivePerformance;
import org.processmining.specpp.prom.events.StagedComputationListeningPanel;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.prom.util.TitledBorderPanel;
import org.processmining.specpp.supervision.Supervisor;

import java.awt.*;
import java.util.Optional;

public class DiscoveryPanel extends AbstractStagePanel<DiscoveryController> {

    private final SPECpp<Place, AdvancedComposition<Place>, PetriNet, ProMPetrinetWrapper> specpp;

    public DiscoveryPanel(DiscoveryController discoveryController) {
        super(discoveryController, new GridBagLayout());
        specpp = discoveryController.getSpecpp();

        AdvancedComposition<Place> ir = specpp.getComposer().getIntermediateResult();

        TitledBorderPanel executionPanel = new TitledBorderPanel("Execution");
        ComputationListeningPanel<DiscoveryController.OngoingComputation> discoveryListeningPanel = new ComputationListeningPanel<>("Discovery", discoveryController.getOngoingDiscoveryComputation());
        ComputationListeningPanel<DiscoveryController.OngoingStagedComputation> postProcessingListeningPanel = new StagedComputationListeningPanel<>("Post Processing", discoveryController.getOngoingPostProcessingComputation());
        executionPanel.append(discoveryListeningPanel);
        executionPanel.append(postProcessingListeningPanel);

        LiveCompositionPanel liveCompositionPanel = new LiveCompositionPanel(ir);

        TitledBorderPanel searchSpacePanel = new TitledBorderPanel("Search Space");
        searchSpacePanel.append(new SearchSpacePanel(specpp));

        TitledBorderPanel performanceTablePanel = new TitledBorderPanel("Performance");
        TitledBorderPanel eventsTablePanel = new TitledBorderPanel("Events");


        Optional<Supervisor> livePerf = specpp.getSupervisors()
                                              .stream()
                                              .filter(s -> s instanceof LivePerformance)
                                              .findFirst();

        Optional<Supervisor> liveEvents = specpp.getSupervisors()
                                                .stream()
                                                .filter(s -> s instanceof LiveEvents)
                                                .findFirst();

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.gridx = 0;
        add(executionPanel, c);
        c.weighty = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.VERTICAL;
        c.gridheight = 4;
        c.gridy++;
        add(liveCompositionPanel, c);
        c.gridx = 1;
        c.gridy = 0;
        add(searchSpacePanel, c);
        c.gridy++;
        add(performanceTablePanel, c);
        c.gridy++;
        add(eventsTablePanel, c);


    }


}
