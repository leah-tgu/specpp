package org.processmining.specpp.prom.mvc.preprocessing;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.processmining.framework.util.ui.widgets.ProMList;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.impls.Factory;
import org.processmining.specpp.datastructures.util.ImmutablePair;
import org.processmining.specpp.datastructures.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

public class PreviewPanel extends JPanel {

    private final PreProcessingController controller;
    private final DefaultListModel<Activity> postsetListModel;
    private final DefaultListModel<Activity> presetListModel;
    private final ProMList<Activity> presetList;
    private final ProMList<Activity> postsetList;
    private final JButton applyButton;

    public PreviewPanel(PreProcessingController controller) {
        super(new GridBagLayout());
        this.controller = controller;

        presetList = new ProMList<>("Preset Activities");
        postsetList = new ProMList<>("Postset Activities");
        presetList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        postsetList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        presetListModel = new DefaultListModel<>();
        postsetListModel = new DefaultListModel<>();
        presetList.getList().setModel(presetListModel);
        postsetList.getList().setModel(postsetListModel);
        presetList.setPreferredSize(new Dimension(200, 200));
        postsetList.setPreferredSize(new Dimension(200, 200));

        applyButton = SlickerFactory.instance().createButton("apply");
        applyButton.addActionListener(e -> {
            controller.applyWorker(collectSelectedActivities());
        });

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        add(presetList, c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.EAST;
        add(postsetList, c);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.SOUTH;
        add(applyButton, c);
    }


    public Pair<Set<Activity>> collectSelectedActivities() {
        return new ImmutablePair<>(new HashSet<>(presetList.getSelectedValuesList()), new HashSet<>(postsetList.getSelectedValuesList()));
    }

    public void updateLists(Collection<Activity> activities, Pair<Comparator<Activity>> comparators) {
        new SwingWorker<Pair<List<Activity>>, Void>() {

            @Override
            protected Pair<List<Activity>> doInBackground() throws Exception {
                List<Activity> l1 = new ArrayList<>(activities);
                l1.remove(Factory.ARTIFICIAL_END);
                l1.sort(comparators.first());
                List<Activity> l2 = new ArrayList<>(activities);
                l2.remove(Factory.ARTIFICIAL_START);
                l2.sort(comparators.second());
                return new ImmutablePair<>(l1, l2);
            }

            @Override
            protected void done() {
                try {
                    Pair<List<Activity>> pair = get();
                    presetListModel.clear();
                    postsetListModel.clear();
                    pair.first().forEach(presetListModel::addElement);
                    pair.second().forEach(postsetListModel::addElement);
                    presetList.setSelectedIndices(IntStream.range(0, presetListModel.size()).toArray());
                    postsetList.setSelectedIndices(IntStream.range(0, postsetListModel.size()).toArray());
                } catch (InterruptedException | ExecutionException ignored) {

                }
            }
        }.execute();
    }


    public void disableButton() {
        SwingUtilities.invokeLater(() -> applyButton.setEnabled(false));
    }

    public void enableButton() {
        SwingUtilities.invokeLater(() -> applyButton.setEnabled(true));
    }
}
