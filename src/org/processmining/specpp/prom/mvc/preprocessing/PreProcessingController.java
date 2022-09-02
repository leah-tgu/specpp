package org.processmining.specpp.prom.mvc.preprocessing;

import com.google.common.collect.ImmutableList;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.specpp.datastructures.encoding.IntEncodings;
import org.processmining.specpp.datastructures.log.Activity;
import org.processmining.specpp.datastructures.log.Log;
import org.processmining.specpp.datastructures.petri.Transition;
import org.processmining.specpp.datastructures.util.Pair;
import org.processmining.specpp.datastructures.util.Tuple2;
import org.processmining.specpp.orchestra.PreProcessingParameters;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.preprocessing.XLogBasedInputDataBundle;
import org.processmining.specpp.preprocessing.orderings.ActivityOrderingBuilder;
import org.processmining.specpp.prom.mvc.AbstractStageController;
import org.processmining.specpp.prom.mvc.SPECppController;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class PreProcessingController extends AbstractStageController {

    private final XLog rawLog;
    private ParametersPanel parametersPanel;
    private PreviewPanel previewPanel;
    private VariantPanel variantPanel;
    private SwingWorker<Pair<Comparator<Activity>>, Void> preprocessingWorker;
    private SwingWorker<InputDataBundle, Void> applicationWorker;

    public PreProcessingController(SPECppController parentController) {
        super(parentController);
        this.rawLog = parentController.getRawLog();
    }

    public PreProcessingPanel createPreProcessingPanel() {
        variantPanel = createVariantVisualizationPanel();
        parametersPanel = createParametersPanel();
        previewPanel = createPreviewPanel();
        return new PreProcessingPanel(this, variantPanel, parametersPanel, previewPanel);
    }

    public VariantPanel createVariantVisualizationPanel() {
        return new VariantPanel();
    }

    public ParametersPanel createParametersPanel() {
        List<XEventClassifier> classifiers = rawLog.getClassifiers();
        return new ParametersPanel(this, classifiers.isEmpty() ? ImmutableList.of(new XEventNameClassifier()) : classifiers);
    }

    public PreviewPanel createPreviewPanel() {
        return new PreviewPanel(this);
    }


    private PreProcessingParameters lastParameters;
    private Pair<Comparator<Activity>> lastComparators;
    private Tuple2<Log, Map<String, Activity>> lastDerivedLog;

    public void preview(PreProcessingParameters collectedParameters) {
        previewWorker(collectedParameters);
    }


    protected SwingWorker<Pair<Comparator<Activity>>, Void> previewWorker(PreProcessingParameters collectedParameters) {
        if (preprocessingWorker != null && !preprocessingWorker.isDone()) preprocessingWorker.cancel(true);
        preprocessingWorker = new SwingWorker<Pair<Comparator<Activity>>, Void>() {

            @Override
            protected Pair<Comparator<Activity>> doInBackground() throws Exception {
                parametersPanel.disableButton();
                if (lastParameters == null || lastParameters.isAddStartEndTransitions() != collectedParameters.isAddStartEndTransitions() || !lastParameters.getEventClassifier()
                                                                                                                                                            .equals(collectedParameters.getEventClassifier())) {
                    lastDerivedLog = XLogBasedInputDataBundle.convertLog(rawLog, collectedParameters.getEventClassifier(), collectedParameters.isAddStartEndTransitions());
                    lastParameters = collectedParameters;
                }
                Pair<Comparator<Activity>> comparators = XLogBasedInputDataBundle.createOrderings(lastDerivedLog.getT1(), lastDerivedLog.getT2(), collectedParameters.getTransitionEncodingsBuilderClass());
                lastComparators = comparators;
                return comparators;
            }

            @Override
            protected void done() {
                try {
                    if (!isCancelled()) {
                        Pair<Comparator<Activity>> comparators = get();
                        Collection<Activity> activities = lastDerivedLog.getT2().values();
                        previewPanel.updateLists(activities, comparators);
                        variantPanel.updateLog(lastDerivedLog.getT1());
                    }
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                } finally {
                    parametersPanel.enableButton();
                }
            }
        };
        preprocessingWorker.execute();
        return preprocessingWorker;
    }


    public void apply(Pair<Set<Activity>> selectedActivities) {
        applyWorker(selectedActivities);
    }

    public SwingWorker<InputDataBundle, Void> applyWorker(Pair<Set<Activity>> selectedActivities) {
        if (applicationWorker != null && !applicationWorker.isDone()) applicationWorker.cancel(true);
        applicationWorker = new SwingWorker<InputDataBundle, Void>() {

            @Override
            protected InputDataBundle doInBackground() throws Exception {
                previewPanel.disableButton();
                SwingWorker<Pair<Comparator<Activity>>, Void> w = previewWorker(parametersPanel.collectParameters());
                w.get();
                if (w.isCancelled()) throw new RuntimeException("data dependency failed to compute");

                BidiMap<Activity, Transition> transitionMapping = new DualHashBidiMap<>();
                lastDerivedLog.getT2()
                              .forEach((label, activity) -> transitionMapping.put(activity, XLogBasedInputDataBundle.makeTransition(activity, label)));
                IntEncodings<Transition> encodings = ActivityOrderingBuilder.createEncodings(selectedActivities, lastComparators, transitionMapping);
                return new InputDataBundle(lastDerivedLog.getT1(), encodings, transitionMapping);
            }

            @Override
            protected void done() {
                try {
                    if (!isCancelled()) {
                        InputDataBundle bundle = get();
                        parentController.preprocessingCompleted(bundle);
                    }
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    previewPanel.enableButton();
                }

            }
        };
        applicationWorker.execute();
        return applicationWorker;
    }

    @Override
    public JPanel createPanel() {
        return createPreProcessingPanel();
    }
}
