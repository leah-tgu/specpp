package org.processmining.specpp.prom.mvc.discovery;

import org.processmining.graphvisualizers.algorithms.GraphVisualizerAlgorithm;
import org.processmining.plugins.graphviz.visualisation.DotPanel;
import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetBuilder;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.util.ColorScheme;
import org.processmining.specpp.prom.util.Destructible;
import org.processmining.specpp.prom.util.FactoryUtils;
import org.processmining.specpp.prom.util.LabeledComboBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.concurrent.ExecutionException;

public class LiveCompositionPanel extends JPanel implements Destructible {

    private final AdvancedComposition<Place> composition;
    private final JPanel content;
    private Timer updateTimer;
    private DotPanel dotPanel;
    private SwingWorker<DotPanel, Void> updateWorker;

    public LiveCompositionPanel(AdvancedComposition<Place> composition) {
        super(new BorderLayout());
        this.composition = composition;
        setBorder(BorderFactory.createRaisedSoftBevelBorder());
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(true);
        header.setBackground(ColorScheme.lightBlue);
        header.add(FactoryUtils.createHeader("Intermediate Result"), BorderLayout.WEST);
        LabeledComboBox<Double> rfRate = FactoryUtils.labeledComboBox("refresh rate", new Double[]{0.25, 0.5, 0.75, 1d, 2d, 3d, 5d, 10d});
        header.add(rfRate, BorderLayout.EAST);
        rfRate.getComboBox().addItemListener(e -> {
            if (e.getItem() != null && e.getStateChange() == ItemEvent.SELECTED) setRefreshRate((Double) e.getItem());
        });

        add(header, BorderLayout.NORTH);
        content = new JPanel();
        content.setDoubleBuffered(true);
        add(content, BorderLayout.CENTER);

        rfRate.getComboBox().setSelectedItem(1.0);
    }

    private void setRefreshRate(double rate) {
        int millis = (int) (1000 / rate);
        if (updateTimer == null) updateTimer = new Timer(millis, e-> updateVisualization());
        else updateTimer.setDelay(millis);
        updateTimer.restart();
    }

    private void updateVisualization() {
        if (updateWorker != null && !updateWorker.isDone()) updateWorker.cancel(true);
        updateWorker = new SwingWorker<DotPanel, Void>() {

            @Override
            protected DotPanel doInBackground() throws Exception {

                ProMPetrinetBuilder pnb = new ProMPetrinetBuilder(composition.toSet());
                ProMPetrinetWrapper wrapper = pnb.build();
                GraphVisualizerAlgorithm alg = new GraphVisualizerAlgorithm();
                JComponent apply = alg.apply(null, wrapper.getNet());
                return (DotPanel) apply;
            }

            @Override
            protected void done() {
                try {
                    DotPanel panel = get();
                    if (!isCancelled()) setDotPanel(panel);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        updateWorker.execute();
    }

    private void setDotPanel(DotPanel panel) {
        dotPanel = panel;
        content.removeAll();
        content.add(dotPanel, BorderLayout.CENTER);
    }

    @Override
    public void destroy() {
        updateTimer.stop();
    }
}
