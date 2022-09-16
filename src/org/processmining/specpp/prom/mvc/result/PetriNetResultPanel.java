package org.processmining.specpp.prom.mvc.result;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.google.common.collect.ImmutableMultimap;
import org.processmining.framework.util.ui.widgets.ProMTable;
import org.processmining.specpp.base.Evaluator;
import org.processmining.specpp.datastructures.encoding.BitMask;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.datastructures.vectorization.IntVector;
import org.processmining.specpp.evaluation.fitness.BasicFitnessEvaluation;
import org.processmining.specpp.evaluation.fitness.DetailedFitnessEvaluation;
import org.processmining.specpp.prom.mvc.discovery.LivePlacesGraph;
import org.processmining.specpp.prom.mvc.swing.SwingFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PetriNetResultPanel extends JSplitPane {


    private final DefaultTableModel tableModel;
    private final JLabel infoLabel;
    private final IntVector variantFrequencies;

    public PetriNetResultPanel(PetriNet petriNet, Evaluator<Place, DetailedFitnessEvaluation> evaluator, IntVector variantFrequencies) {
        super(JSplitPane.HORIZONTAL_SPLIT);
        this.variantFrequencies = variantFrequencies;

        JPanel left = new JPanel(new BorderLayout());
        left.add(Box.createHorizontalStrut(400), BorderLayout.PAGE_END);
        setLeftComponent(left);
        String overfedSymbol = "\u25B3(L)";
        String underfedSymbol = "\u25BD(L)";
        String fittingSymbol = "\u25A1(L)";
        String rel = "^r";
        tableModel = SwingFactory.readOnlyTableModel(new String[]{"Size", "Preset", "Postset", fittingSymbol, underfedSymbol, overfedSymbol, fittingSymbol + rel, underfedSymbol + rel, overfedSymbol + rel}, ImmutableMultimap.<Class<?>, Integer>builder()
                                                                                                                                                                                                                               .putAll(String.class, 1, 2)
                                                                                                                                                                                                                               .putAll(Integer.class, 0)
                                                                                                                                                                                                                               .putAll(Double.class, 3, 4, 5, 6, 7, 8)
                                                                                                                                                                                                                               .build());
        ProMTable proMTable = SwingFactory.proMTable(tableModel);
        proMTable.getColumnModel().getColumn(0).setMaxWidth(50);
        proMTable.getColumnModel().getColumn(3).setMaxWidth(80);
        proMTable.getColumnModel().getColumn(4).setMaxWidth(80);
        proMTable.getColumnModel().getColumn(5).setMaxWidth(80);
        JPanel right = new JPanel(new BorderLayout());
        right.add(proMTable, BorderLayout.CENTER);
        JPanel bottomLine = new JPanel();
        bottomLine.setLayout(new BoxLayout(bottomLine, BoxLayout.LINE_AXIS));
        bottomLine.add(SlickerFactory.instance().createLabel(String.format("Size: %d", petriNet.size())));
        infoLabel = SlickerFactory.instance().createLabel("not yet computed");
        bottomLine.add(infoLabel);
        right.add(bottomLine, BorderLayout.PAGE_END);
        setRightComponent(right);


        new SwingWorker<JComponent, Void>() {

            @Override
            protected JComponent doInBackground() throws Exception {
                LivePlacesGraph graph = new LivePlacesGraph();
                graph.update(ProMPetrinetWrapper.of(petriNet));
                return graph.getComponent();
            }

            @Override
            protected void done() {
                try {
                    JComponent jComponent = get();
                    if (!isCancelled()) left.add(jComponent, BorderLayout.CENTER);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }.execute();

        new SwingWorker<Map<Place, DetailedFitnessEvaluation>, Tuple2<Place, DetailedFitnessEvaluation>>() {

            @Override
            protected Map<Place, DetailedFitnessEvaluation> doInBackground() throws Exception {
                return petriNet.getPlaces().stream().collect(Collectors.toMap(p -> p, evaluator));
            }

            @Override
            protected void done() {
                try {
                    Map<Place, DetailedFitnessEvaluation> map = get();
                    if (!isCancelled()) {
                        updateTable(map);
                    }
                } catch (InterruptedException | ExecutionException ignored) {
                    ignored.printStackTrace();
                }
            }
        }.execute();
    }

    private void updateTable(Map<Place, DetailedFitnessEvaluation> map) {
        if (map == null) {
            infoLabel.setText("computation failed");
            return;
        }

        tableModel.setRowCount(0);
        BitMask overallFittingVariants = null;
        for (Map.Entry<Place, DetailedFitnessEvaluation> entry : map.entrySet()) {
            Place key = entry.getKey();
            DetailedFitnessEvaluation value = entry.getValue();
            BitMask fittingVariants = value.getFittingVariants();
            if (overallFittingVariants == null) overallFittingVariants = fittingVariants;
            else overallFittingVariants.intersection(fittingVariants);
            BasicFitnessEvaluation fractions = value.getFractionalEvaluation();
            tableModel.addRow(new Object[]{key.size(), key.preset().toString(), key.postset().toString(), fractions.getFittingFraction(), fractions.getUnderfedFraction(), fractions.getOverfedFraction(), fractions.getRelativeFittingFraction(), fractions.getRelativeUnderfedFraction(), fractions.getRelativeOverfedFraction()});
        }
        double sum = overallFittingVariants == null ? Double.NaN : overallFittingVariants.stream()
                                                                                         .mapToDouble(variantFrequencies::getRelative)
                                                                                         .sum();
        infoLabel.setText(String.format("Combined Fitting Traces Fraction: %.2f", sum));
        tableModel.fireTableDataChanged();
    }


}
