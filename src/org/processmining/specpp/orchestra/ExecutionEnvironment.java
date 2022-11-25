package org.processmining.specpp.orchestra;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import org.processmining.specpp.base.Candidate;
import org.processmining.specpp.base.Result;
import org.processmining.specpp.base.impls.SPECpp;
import org.processmining.specpp.componenting.system.link.CompositionComponent;
import org.processmining.specpp.config.parameters.ExecutionParameters;
import org.processmining.specpp.prom.computations.OngoingComputation;
import org.processmining.specpp.prom.computations.OngoingStagedComputation;
import org.processmining.specpp.traits.Joinable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class ExecutionEnvironment implements Joinable, AutoCloseable {

    private final ExecutorService executorService;
    private final int MAX_TERMINATION_WAIT = 10;


    @Override
    public void join() throws InterruptedException {
        for (ListenableFuture<?> f : monitoredFutures) {
            try {
                f.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        executorService.shutdown();
        controlThreadService.shutdown();
        executorService.awaitTermination(MAX_TERMINATION_WAIT, TimeUnit.SECONDS);
    }

    @Override
    public void close() throws InterruptedException {
        join();
    }

    public static class SPECppExecution<C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> {

        private final SPECpp<C, I, R, F> specpp;
        private final OngoingComputation masterComputation;
        private final OngoingComputation discoveryComputation;
        private final OngoingStagedComputation postProcessingComputation;
        private final List<ScheduledFuture<?>> timeKeepingFutures;

        public SPECppExecution(SPECpp<C, I, R, F> specpp, OngoingComputation masterComputation, OngoingComputation discoveryComputation, OngoingStagedComputation postProcessingComputation, List<ScheduledFuture<?>> timeKeepingFutures) {
            this.specpp = specpp;
            this.masterComputation = masterComputation;
            this.discoveryComputation = discoveryComputation;
            this.postProcessingComputation = postProcessingComputation;
            this.timeKeepingFutures = timeKeepingFutures;
        }

        public SPECpp<C, I, R, F> getSPECpp() {
            return specpp;
        }

        public OngoingComputation getDiscoveryComputation() {
            return discoveryComputation;
        }

        public OngoingStagedComputation getPostProcessingComputation() {
            return postProcessingComputation;
        }

        public List<ScheduledFuture<?>> getTimeKeepingFutures() {
            return timeKeepingFutures;
        }

        public boolean hasTerminatedSuccessfully() {
            return discoveryComputation.hasTerminatedSuccessfully() && postProcessingComputation.hasTerminatedSuccessfully() && masterComputation.hasTerminatedSuccessfully();
        }

        public OngoingComputation getMasterComputation() {
            return masterComputation;
        }

    }

    private final ScheduledExecutorService controlThreadService;
    private final List<ListenableFuture<?>> monitoredFutures;

    public ExecutionEnvironment() {
        this(2);
    }

    public ExecutionEnvironment(int num_threads) {
        this(Executors.newFixedThreadPool(Math.max(num_threads - 1, 1)), Executors.newScheduledThreadPool(1));
    }

    public ExecutionEnvironment(ExecutorService executorService, ScheduledExecutorService controlThreadService) {
        this.executorService = executorService;
        this.controlThreadService = controlThreadService;
        monitoredFutures = new ArrayList<>();
    }

    public static <C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> SPECppExecution<C, I, R, F> oneshotExecution(SPECpp<C, I, R, F> specpp, ExecutionParameters executionParameters) {
        SPECppExecution<C, I, R, F> execution;
        try (ExecutionEnvironment ee = new ExecutionEnvironment(2)) {
            execution = ee.execute(specpp, executionParameters);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return execution;
    }

    public <C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> ListenableFuture<?> addCompletionCallback(SPECppExecution<C, I, R, F> execution, Consumer<SPECppExecution<C, I, R, F>> callback) {
        ListenableFutureTask<?> futureTask = ListenableFutureTask.create(() -> callback.accept(execution), null);

        execution.getMasterComputation().getComputationFuture().addListener(futureTask, controlThreadService);
        return futureTask;
    }


    public <C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> SPECppExecution<C, I, R, F> execute(SPECpp<C, I, R, F> specpp, ExecutionParameters executionParameters) {

        OngoingComputation masterComputation = new OngoingComputation();
        OngoingComputation oc = new OngoingComputation();
        OngoingStagedComputation osc = new OngoingStagedComputation(specpp.getPostProcessor().getPipelineLength());
        List<ScheduledFuture<?>> timeKeepingFutures = new LinkedList<>();
        SPECppExecution<C, I, R, F> execution = new SPECppExecution<>(specpp, masterComputation, oc, osc, timeKeepingFutures);


        Runnable discoveryCanceller = () -> {
            specpp.cancelGracefully();
            oc.markGracefullyCancelled();
        };
        Runnable postProcessingCanceller = () -> {
            osc.getComputationFuture().cancel(true);
        };
        Runnable totalCanceller = () -> {
            oc.getComputationFuture().cancel(true);
            osc.getComputationFuture().cancel(true);
        };
        oc.setCancellationCallback(discoveryCanceller);
        osc.setCancellationCallback(postProcessingCanceller);
        masterComputation.setCancellationCallback(totalCanceller);

        ExecutionParameters.ExecutionTimeLimits timeLimits = executionParameters.getTimeLimits();
        oc.setTimeLimit(timeLimits.getDiscoveryTimeLimit());
        osc.setTimeLimit(timeLimits.getPostProcessingTimeLimit());
        masterComputation.setTimeLimit(timeLimits.getTotalTimeLimit());
        ListenableFutureTask<R> discoveryFuture = ListenableFutureTask.create(specpp::executeDiscovery);
        oc.setComputationFuture(discoveryFuture);
        ListenableFutureTask<F> postProcessingFuture = ListenableFutureTask.create(specpp::executePostProcessing);
        osc.setComputationFuture(postProcessingFuture);

        ListenableFutureTask<Boolean> task = ListenableFutureTask.create(() -> {
            ScheduledFuture<?> totalCancellationFuture = null;
            if (timeLimits.hasTotalTimeLimit())
                totalCancellationFuture = controlThreadService.schedule(totalCanceller, timeLimits.getTotalTimeLimit()
                                                                                                  .toMillis(), TimeUnit.MILLISECONDS);

            masterComputation.markStarted();
            oc.markStarted();
            specpp.start();

            try {

                ScheduledFuture<?> discoveryCancellationFuture = null;
                if (timeLimits.hasDiscoveryTimeLimit())
                    discoveryCancellationFuture = controlThreadService.schedule(discoveryCanceller, timeLimits.getDiscoveryTimeLimit()
                                                                                                              .toMillis(), TimeUnit.MILLISECONDS);

                discoveryFuture.run();
                discoveryFuture.get();

                oc.markEnded();
                if (discoveryCancellationFuture != null) discoveryCancellationFuture.cancel(true);
            } catch (InterruptedException | ExecutionException e) {
                specpp.stop();
                oc.markForciblyCancelled();
                oc.markEnded();
                postProcessingFuture.cancel(true);
            }

            osc.markStarted();
            try {

                postProcessingFuture.run();
                if (timeLimits.hasPostProcessingTimeLimit())
                    postProcessingFuture.get(timeLimits.getPostProcessingTimeLimit().toMillis(), TimeUnit.MILLISECONDS);
                else postProcessingFuture.get();

                osc.markEnded();
                specpp.stop();

            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                osc.markForciblyCancelled();
                osc.markEnded();
                specpp.stop();
                masterComputation.markForciblyCancelled();
            } finally {
                if (totalCancellationFuture != null) totalCancellationFuture.cancel(true);
            }

            masterComputation.markEnded();

            return true;
        });
        masterComputation.setComputationFuture(task);

        executorService.submit(task);
        monitoredFutures.add(task);

        return execution;
    }


}
