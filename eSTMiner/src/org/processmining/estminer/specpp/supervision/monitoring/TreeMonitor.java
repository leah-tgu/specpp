package org.processmining.estminer.specpp.supervision.monitoring;

import com.google.common.collect.ImmutableList;
import org.processmining.estminer.specpp.config.parameters.TreeTrackerParameters;
import org.processmining.estminer.specpp.representations.petri.Place;
import org.processmining.estminer.specpp.representations.tree.base.PropertyNode;
import org.processmining.estminer.specpp.representations.tree.base.impls.ChangeTree;
import org.processmining.estminer.specpp.representations.tree.base.impls.TreePrinter;
import org.processmining.estminer.specpp.representations.tree.events.TreeNodeEvent;
import org.processmining.estminer.specpp.supervision.observations.Visualization;
import org.processmining.estminer.specpp.supervision.piping.TreeDrawer;
import org.processmining.estminer.specpp.util.JavaTypingUtils;
import org.processmining.estminer.specpp.util.datastructures.TypedItem;

import java.util.Collection;

public class TreeMonitor implements MultiComputingMonitor<TreeNodeEvent<PropertyNode<Place>>, ChangeTree<Place>> {

    private final ChangeTree<Place> cht;
    private final TreeTrackerParameters parameters;
    private int observedEvents;

    public TreeMonitor(TreeTrackerParameters parameters) {
        this.parameters = parameters;
        this.observedEvents = 0;
        cht = new ChangeTree<>();
    }

    @Override
    public ChangeTree<Place> getMonitoringState() {
        return cht;
    }

    @Override
    public void handleObservation(TreeNodeEvent<PropertyNode<Place>> observation) {
        if (observedEvents++ < parameters.getEventLimit()) cht.observe(observation);
    }


    @Override
    public Collection<TypedItem<?>> computeResults() {
        TreePrinter printer = new TreePrinter(cht, parameters.getFromLevel(), parameters.getToLevel(), parameters.getPrintNodeLimit());
        TreeDrawer drawer = new TreeDrawer(cht, parameters.getFromLevel(), parameters.getToLevel(), parameters.getDrawNodeLimit(), "Candidate Tree");
        return ImmutableList.of(new TypedItem<>(String.class, printer.computeObservation()), new TypedItem<>(JavaTypingUtils.castClass(Visualization.class), drawer.computeObservation()));
    }
}
