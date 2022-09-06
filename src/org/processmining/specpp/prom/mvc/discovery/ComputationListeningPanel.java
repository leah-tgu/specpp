package org.processmining.specpp.prom.mvc.discovery;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.specpp.prom.computations.ComputationEnded;
import org.processmining.specpp.prom.computations.ComputationEvent;
import org.processmining.specpp.prom.computations.ComputationStarted;
import org.processmining.specpp.prom.computations.OngoingComputation;

import javax.swing.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class ComputationListeningPanel<T extends OngoingComputation> extends JPanel {

    protected final String label;
    protected final T ongoingComputation;
    protected final JLabel timingLabel;
    protected final JProgressBar progressBar;
    protected Timer refreshTimer;


    public ComputationListeningPanel(String label, T ongoingComputation) {
        this.label = label;
        this.ongoingComputation = ongoingComputation;

        timingLabel = SlickerFactory.instance().createLabel("");
        progressBar = SlickerFactory.instance().createProgressBar(JProgressBar.HORIZONTAL);

        ongoingComputation.addObserver(this::updateComputation);
        initProgress();
    }

    protected void initProgress() {
        progressBar.setMinimum(0);
        progressBar.setMaximum(1);
    }

    private void updateComputation(ComputationEvent event) {
        updateText(event);
        updateProgress(event);
        updateState(event);
    }

    protected void updateText(ComputationEvent event) {
        LocalDateTime start = ongoingComputation.getStart();
        String newText = "";
        if (start == null) newText = label + " Computation started @?. Running for ? out of ?.";
        else {
            LocalDateTime now = LocalDateTime.now();
            Duration runningTime = Duration.between(start, now);
            Duration timeLimit = ongoingComputation.getTimeLimit();
            newText = label + " Computation started @" + start + ". Running for " + runningTime.toString()
                                                                                                .substring(2) + " out of " + (timeLimit == null ? "unlimited" : timeLimit.toString()
                                                                                                                                                                         .substring(2)) + ".";
            LocalDateTime end = ongoingComputation.getEnd();
            if (end != null) {
                newText += " " + "Finished @" + end + ".";
            }
        }

        String finalNewText = newText;
        SwingUtilities.invokeLater(() -> {
            timingLabel.setText(finalNewText);
        });
    }

    protected void updateProgress(ComputationEvent event) {
        if (event instanceof ComputationStarted) {
            SwingUtilities.invokeLater(() -> progressBar.setIndeterminate(true));
        } else if (event instanceof ComputationEnded) {
            SwingUtilities.invokeLater(() -> {
                progressBar.setIndeterminate(false);
                progressBar.setValue(progressBar.getMaximum());
            });
        }
    }

    private void updateState(ComputationEvent event) {
        if (event instanceof ComputationStarted) {
            refreshTimer = new Timer(100, e -> updateComputation(null));
            refreshTimer.start();
        } else if (event instanceof ComputationEnded) refreshTimer.stop();
    }

}
