package org.processmining.specpp.prom.mvc.discovery;

import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.base.impls.SPECppBuilder;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.config.Configuration;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.orchestra.SPECppConfigBundle;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.prom.events.ComputationEvent;
import org.processmining.specpp.prom.events.ComputationStageCompleted;
import org.processmining.specpp.prom.mvc.AbstractStageController;
import org.processmining.specpp.prom.mvc.SPECppController;
import org.processmining.specpp.supervision.piping.AbstractAsyncAwareObservable;

import javax.swing.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class DiscoveryController extends AbstractStageController {

    private final SPECpp<Place, AdvancedComposition<Place>, PetriNet, ProMPetrinetWrapper> specpp;
    private final DelegatingDataSource<Runnable> cancellationDelegate;
    private final OngoingDiscoveryComputation ongoingDiscoveryComputation;
    private final OngoingStagedComputation ongoingStagedComputation;
    private LocalDateTime startTime;

    public DiscoveryController(SPECppController parentController) {
        super(parentController);
        InputDataBundle dataBundle = parentController.getDataBundle();
        SPECppConfigBundle configBundle = parentController.getConfigBundle();


        GlobalComponentRepository cr = new GlobalComponentRepository();
        cancellationDelegate = new DelegatingDataSource<>();

        cr.require(DataRequirements.dataSource("cancel_gracefully", Runnable.class), cancellationDelegate);
        configBundle.instantiate(cr, dataBundle);
        Configuration configuration = new Configuration(cr);
        specpp = configuration.createFrom(new SPECppBuilder<>(), cr);
        specpp.init();

        ongoingDiscoveryComputation = new OngoingDiscoveryComputation();
        ongoingStagedComputation = new OngoingStagedComputation(specpp.getPostProcessor()
                                                                      .getPipelineLength());
    }

    public SPECpp<Place, AdvancedComposition<Place>, PetriNet, ProMPetrinetWrapper> getSpecpp() {
        return specpp;
    }

    public void startDiscovery() {
        Duration durationLimit = Duration.ofMinutes(1);
        specpp.start();
        startTime = LocalDateTime.now();
        ongoingDiscoveryComputation.setStart(startTime);
        CompletableFuture<Void> future = CompletableFuture.runAsync(specpp::executeDiscovery, parentController.getPluginContext()
                                                                                                              .getExecutor());
        future.thenRun(this::discoveryFinished);
        if (durationLimit != null) {
            Timer cancellationTimer = new Timer((int) durationLimit.toMillis(), e -> cancelDiscoveryComputation());
            cancellationTimer.setRepeats(false);
            cancellationTimer.start();
        }
    }

    private void discoveryFinished() {
        ongoingDiscoveryComputation.setEnd(LocalDateTime.now());
        startPostProcessing();
    }

    public OngoingDiscoveryComputation getOngoingDiscoveryComputation() {
        return ongoingDiscoveryComputation;
    }

    public OngoingStagedComputation getOngoingPostProcessingComputation() {
        return ongoingStagedComputation;
    }

    public static class OngoingComputation extends AbstractAsyncAwareObservable<ComputationEvent> {

        private LocalDateTime start, end;

        public LocalDateTime getStart() {
            return start;
        }

        public void setStart(LocalDateTime start) {
            this.start = start;
        }

        public LocalDateTime getEnd() {
            return end;
        }

        public void setEnd(LocalDateTime end) {
            this.end = end;
        }

        public Duration getTimeLimit() {
            return null;
        }
    }

    public static class OngoingDiscoveryComputation extends OngoingComputation {

        private  Duration timeLimit;

        @Override
        public Duration getTimeLimit() {
            return timeLimit;
        }

        public void setTimeLimit(Duration timeLimit) {
            this.timeLimit = timeLimit;
        }
    }

    public static class OngoingStagedComputation extends OngoingComputation {
        private final int stages;
        private final AtomicInteger lastCompletedStage;

        public OngoingStagedComputation(int stages) {
            this.stages = stages;
            lastCompletedStage = new AtomicInteger(0);
        }
        public int getStageCount() {
            return stages;
        }

        public void incStage() {
            int i = lastCompletedStage.incrementAndGet();
            publish(new ComputationStageCompleted(i));
        }


    }

    private void startPostProcessing() {
        ongoingStagedComputation.setStart(LocalDateTime.now());
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> specpp.executePostProcessing(e -> ongoingStagedComputation.incStage()), parentController.getPluginContext()
                                                                                                                                                                  .getExecutor());
        future.thenRun(this::postProcessingFinished);
    }

    private void postProcessingFinished() {
        ongoingStagedComputation.setEnd(LocalDateTime.now());
        specpp.stop();
        parentController.discoveryCompleted(specpp.getPostProcessedResult());
    }

    public void cancelDiscoveryComputation() {
        cancellationDelegate.getData().run();
    }

    @Override
    public JPanel createPanel() {
        return new DiscoveryPanel(this);
    }


}
