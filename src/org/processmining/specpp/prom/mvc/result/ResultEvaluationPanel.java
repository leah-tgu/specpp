package org.processmining.specpp.prom.mvc.result;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.log.utils.XUtils;
import org.processmining.modelrepair.plugins.align.CostBasedCompleteParamProvider_nonUI;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithoutILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.etconformance.ETCAlgorithm;
import org.processmining.plugins.etconformance.ETCResults;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.pnetreplayer.utils.TransEvClassMappingUtils;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;
import org.processmining.specpp.supervision.supervisors.DebuggingSupervisor;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ResultEvaluationPanel extends AbstractStagePanel<ResultController> {

    private final JLabel fitnessLabel, precisionLabel;

    public ResultEvaluationPanel(ResultController resultController, ProMPetrinetWrapper proMPetrinetWrapper) {
        super(resultController);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(SlickerFactory.instance()
                          .createLabel(String.format("Petri net contains %d transitions, %d places & %d arcs.", proMPetrinetWrapper.getTransitions()
                                                                                                                                   .size(), proMPetrinetWrapper.getPlaces()
                                                                                                                                                               .size(), proMPetrinetWrapper.getEdges()
                                                                                                                                                                                           .size())));
        add(Box.createHorizontalStrut(10));
        fitnessLabel = SlickerFactory.instance().createLabel("Alignment-Based Fitness: ?");
        add(fitnessLabel);
        add(Box.createHorizontalStrut(10));
        precisionLabel = SlickerFactory.instance().createLabel("ETC Precision: ?");
        add(precisionLabel);

        XLog rawLog = resultController.getRawLog();

        SwingWorker<Double, Void> fitnessWorker = new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() throws Exception {
                XLog xLog = rawLog;
                TransEvClassMapping mapping = getTransEvClassMapping(resultController, proMPetrinetWrapper);
                PluginContext context = resultController.getContext().createChildContext("Fitness");
                CostBasedCompleteParamProvider_nonUI provider = new CostBasedCompleteParamProvider_nonUI(context, proMPetrinetWrapper, xLog, mapping);
                IPNReplayParameter params = provider.constructReplayParameter(provider.getDefaultEventCost(), provider.getDefaultTransitionCost(), provider.getDefaultNumOfStates());
                PNRepResult syncReplayResults = new PNLogReplayer().replayLog(context, proMPetrinetWrapper, xLog, mapping, new PetrinetReplayerWithoutILP(), params);
                return (Double) syncReplayResults.getInfo().get(PNRepResult.TRACEFITNESS);
            }

            @Override
            protected void done() {
                try {
                    Double o = get();
                    fitnessLabel.setText(String.format("Alignment-Based Fitness: %.2f", o));
                } catch (InterruptedException | ExecutionException ignored) {
                    fitnessLabel.setText("Alignment-Based Fitness: failed");
                    DebuggingSupervisor.debug("Result Evaluation", "Alignment.based Fitness failed:\n");
                    ignored.printStackTrace();
                }
            }
        };

        fitnessWorker.execute();

        SwingWorker<ETCResults, Void> precisionWorker = new SwingWorker<ETCResults, Void>() {

            @Override
            protected ETCResults doInBackground() throws Exception {
                XLog xLog = rawLog;
                TransEvClassMapping transEvClassMapping = getTransEvClassMapping(resultController, proMPetrinetWrapper);
                PluginContext childContext = resultController.getContext().createChildContext("Precision");
                ETCResults etcResults = new ETCResults();
                ETCAlgorithm.exec(childContext, xLog, proMPetrinetWrapper, proMPetrinetWrapper.getInitialMarking(), transEvClassMapping, etcResults);
                return etcResults;
            }

            @Override
            protected void done() {
                try {
                    ETCResults etcResults = get();
                    precisionLabel.setText(String.format("ETC Precision: %.2f", etcResults.getEtcp()));
                } catch (InterruptedException | ExecutionException ignored) {
                    precisionLabel.setText("ETC Precision: failed");
                    DebuggingSupervisor.debug("Result Evaluation", "ETC Precision failed:\n");
                    ignored.printStackTrace();
                }
            }
        };

        precisionWorker.execute();
    }

    private static TransEvClassMapping getTransEvClassMapping(ResultController resultController, ProMPetrinetWrapper proMPetrinetWrapper) {
        XEventClassifier eventClassifier = resultController.getEventClassifier();
        Set<XEventClass> eventClasses = new HashSet<>(XUtils.createEventClasses(eventClassifier, resultController.getRawLog())
                                                            .getClasses());
        return TransEvClassMappingUtils.getInstance().getMapping(proMPetrinetWrapper, eventClasses, eventClassifier);
    }
}
