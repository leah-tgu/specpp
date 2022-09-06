package org.processmining.specpp.prom.mvc.discovery;

import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.MoreExecutors;
import org.processmining.specpp.base.AdvancedComposition;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.base.impls.SPECppBuilder;
import org.processmining.specpp.componenting.data.DataRequirements;
import org.processmining.specpp.componenting.data.ParameterRequirements;
import org.processmining.specpp.componenting.delegators.DelegatingDataSource;
import org.processmining.specpp.componenting.system.GlobalComponentRepository;
import org.processmining.specpp.config.Configuration;
import org.processmining.specpp.config.parameters.ExecutionParameters;
import org.processmining.specpp.datastructures.petri.PetriNet;
import org.processmining.specpp.datastructures.petri.Place;
import org.processmining.specpp.datastructures.petri.ProMPetrinetWrapper;
import org.processmining.specpp.orchestra.SPECppConfigBundle;
import org.processmining.specpp.preprocessing.InputDataBundle;
import org.processmining.specpp.prom.computations.OngoingComputation;
import org.processmining.specpp.prom.computations.OngoingStagedComputation;
import org.processmining.specpp.prom.mvc.AbstractStageController;
import org.processmining.specpp.prom.mvc.SPECppController;
import org.processmining.specpp.prom.util.Destructible;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

public class DiscoveryController extends AbstractStageController implements Destructible {

    private final SPECpp<Place, AdvancedComposition<Place>, PetriNet, ProMPetrinetWrapper> specpp;
    private final DelegatingDataSource<Runnable> gracefulCancellationDelegate;
    private final OngoingComputation ongoingDiscoveryComputation;
    private final OngoingStagedComputation ongoingPostProcessingComputation;
    private final ExecutionParameters.ExecutionTimeLimits timeLimits;
    private LocalDateTime startTime;
    private ListenableFutureTask<ProMPetrinetWrapper> postProcessingFutureTask;
    private List<Timer> startedTimers = new LinkedList<>();

    public DiscoveryController(SPECppController parentController) {
        super(parentController);
        InputDataBundle dataBundle = parentController.getDataBundle();
        SPECppConfigBundle configBundle = parentController.getConfigBundle();


        GlobalComponentRepository gcr = new GlobalComponentRepository();
        gracefulCancellationDelegate = new DelegatingDataSource<>();

        gcr.require(DataRequirements.dataSource("cancel_gracefully", Runnable.class), gracefulCancellationDelegate);
        configBundle.instantiate(gcr, dataBundle);
        Configuration configuration = new Configuration(gcr);

        ExecutionParameters executionParameters = gcr.parameters()
                                                     .askForData(ParameterRequirements.EXECUTION_PARAMETERS);
        timeLimits = executionParameters.getTimeLimits();

        specpp = configuration.createFrom(new SPECppBuilder<>(), gcr);
        specpp.init();

        ongoingDiscoveryComputation = new OngoingComputation();
        ongoingPostProcessingComputation = new OngoingStagedComputation(specpp.getPostProcessor().getPipelineLength());
        startDiscovery();
    }

    public SPECpp<Place, AdvancedComposition<Place>, PetriNet, ProMPetrinetWrapper> getSpecpp() {
        return specpp;
    }

    public void startDiscovery() {
        if (ongoingDiscoveryComputation.isCancelled()) return;

        specpp.start();

        startTime = LocalDateTime.now();
        if (timeLimits.hasDiscoveryTimeLimit())
            ongoingDiscoveryComputation.setTimeLimit(timeLimits.getDiscoveryTimeLimit());
        else if (timeLimits.hasTotalTimeLimit())
            ongoingDiscoveryComputation.setTimeLimit(timeLimits.getTotalTimeLimit());
        ongoingDiscoveryComputation.setStart(startTime);
        ListenableFutureTask<PetriNet> discoveryFutureTask = ListenableFutureTask.create(specpp::executeDiscovery);
        discoveryFutureTask.addListener(this::discoveryFinished, MoreExecutors.sameThreadExecutor());
        getExecutor().execute(discoveryFutureTask);
        if (timeLimits.hasDiscoveryTimeLimit()) {
            Timer cancellationTimer = new Timer((int) timeLimits.getDiscoveryTimeLimit()
                                                                .toMillis(), e -> cancelDiscoveryComputation());
            cancellationTimer.setRepeats(false);
            startedTimers.add(cancellationTimer);
            cancellationTimer.start();
        }
        if (timeLimits.hasTotalTimeLimit()) {
            Timer cancellationTimer = new Timer((int) timeLimits.getTotalTimeLimit()
                                                                .toMillis(), e -> cancelEverything());
            cancellationTimer.setRepeats(false);
            startedTimers.add(cancellationTimer);
            cancellationTimer.start();
        }
    }

    private void discoveryFinished() {
        ongoingDiscoveryComputation.setEnd(LocalDateTime.now());
        startPostProcessing();
    }

    public OngoingComputation getOngoingDiscoveryComputation() {
        return ongoingDiscoveryComputation;
    }

    public OngoingStagedComputation getOngoingPostProcessingComputation() {
        return ongoingPostProcessingComputation;
    }

    private void startPostProcessing() {
        if (ongoingPostProcessingComputation.isCancelled()) return;
        ongoingPostProcessingComputation.setStart(LocalDateTime.now());
        postProcessingFutureTask = ListenableFutureTask.create(() -> specpp.executePostProcessing(e -> ongoingPostProcessingComputation.incStage()));
        postProcessingFutureTask.addListener(this::postProcessingFinished, MoreExecutors.sameThreadExecutor());
        getExecutor().execute(postProcessingFutureTask);

        if (timeLimits.hasPostProcessingTimeLimit()) {
            Timer cancellationTimer = new Timer(((int) timeLimits.getPostProcessingTimeLimit()
                                                                 .toMillis()), e -> cancelPostProcessingComputation());
            cancellationTimer.setRepeats(false);
            startedTimers.add(cancellationTimer);
            cancellationTimer.start();
        }
    }

    private Executor getExecutor() {
        return parentController.getPluginContext().getExecutor();
    }

    private void postProcessingFinished() {
        ongoingPostProcessingComputation.setEnd(LocalDateTime.now());
        specpp.stop();
        if (!ongoingPostProcessingComputation.isCancelled()) {
            parentController.discoveryCompleted(specpp.getPostProcessedResult());
            System.out.println("DiscoveryController.postProcessingFinished.success");
        } else System.out.println("DiscoveryController.postProcessingFinished.cancellation");
    }

    public void cancelDiscoveryComputation() {
        gracefulCancellationDelegate.getData().run();
        ongoingDiscoveryComputation.setGracefullyCancelled();
    }

    public void cancelPostProcessingComputation() {
        System.out.println("DiscoveryController.cancelPostProcessingComputation");
        if (postProcessingFutureTask != null && !postProcessingFutureTask.isDone())
            postProcessingFutureTask.cancel(true);
        ongoingPostProcessingComputation.setForciblyCancelled();
    }

    private void cancelEverything() {
        cancelDiscoveryComputation();
        cancelPostProcessingComputation();
    }

    @Override
    public JPanel createPanel() {
        return new DiscoveryPanel(this);
    }


    @Override
    public void destroy() {
        cancelEverything();
        for (Timer timer : startedTimers) {
            timer.stop();
        }
    }
}
