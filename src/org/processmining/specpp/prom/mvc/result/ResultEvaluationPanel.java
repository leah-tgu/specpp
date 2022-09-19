package org.processmining.specpp.prom.mvc.result;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.log.utils.XUtils;
import org.processmining.modelrepair.plugins.align.CostBasedCompleteParamProvider_nonUI;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.pnetreplayer.utils.TransEvClassMappingUtils;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.prom.mvc.AbstractStagePanel;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ResultEvaluationPanel extends AbstractStagePanel<ResultController> {

    private final JLabel fitnessLabel;

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

        SwingWorker<Object, Void> worker = new SwingWorker<Object, Void>() {
            @Override
            protected Object doInBackground() throws Exception {
                XLog xLog = resultController.getRawLog();
                XEventClassifier eventClassifier = resultController.getEventClassifier();
                Set<XEventClass> eventClasses = new HashSet<>(XUtils.createEventClasses(eventClassifier, xLog)
                                                                    .getClasses());
                TransEvClassMapping mapping = TransEvClassMappingUtils.getInstance()
                                                                      .getMapping(proMPetrinetWrapper, eventClasses, eventClassifier);
                PluginContext context = resultController.getContext().createChildContext("Fitness");
                CostBasedCompleteParamProvider_nonUI provider = new CostBasedCompleteParamProvider_nonUI(context, proMPetrinetWrapper, xLog, mapping);
                IPNReplayParameter params = provider.constructReplayParameter(provider.getDefaultEventCost(), provider.getDefaultTransitionCost(), provider.getDefaultNumOfStates());
                PNRepResult syncReplayResults = new PNLogReplayer().replayLog(context, proMPetrinetWrapper, xLog, mapping, new PetrinetReplayerWithILP(), params);
                return syncReplayResults.getInfo().get(PNRepResult.TRACEFITNESS);
            }

            @Override
            protected void done() {
                try {
                    Object o = get();
                    fitnessLabel.setText(String.format("Alignment-Based Fitness: " + o + " (" + o.getClass() + ")"));
                } catch (InterruptedException | ExecutionException ignored) {
                    fitnessLabel.setText("Alignment-Based Fitness: failed");
                }
            }
        };

        worker.execute();

    }
}
