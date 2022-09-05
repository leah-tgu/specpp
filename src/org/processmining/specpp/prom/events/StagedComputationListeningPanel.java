package org.processmining.specpp.prom.events;

import org.processmining.specpp.prom.mvc.discovery.ComputationListeningPanel;
import org.processmining.specpp.prom.mvc.discovery.DiscoveryController;

import javax.swing.*;

public class StagedComputationListeningPanel<T extends DiscoveryController.OngoingStagedComputation> extends ComputationListeningPanel<DiscoveryController.OngoingStagedComputation> {
    public StagedComputationListeningPanel(String label, DiscoveryController.OngoingStagedComputation ongoingComputation) {
        super(label, ongoingComputation);
    }

    @Override
    protected void initProgress() {
        progressBar.setMinimum(0);
        progressBar.setMaximum(ongoingComputation.getStageCount());
    }

    @Override
    protected void updateProgress(ComputationEvent event) {
        if (event instanceof ComputationStageCompleted)
            SwingUtilities.invokeLater(() -> progressBar.setValue(((ComputationStageCompleted) event).getCompletedStage()));
    }
}
