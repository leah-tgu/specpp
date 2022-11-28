package org.processmining.specpp.orchestra;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import org.apache.commons.lang3.ThreadUtils;
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

    private final ScheduledExecutorService timeoutExecutorService;
    private final ExecutorService managerExecutorService, workerExecutorService, callbackExecutorService;
    private final List<ListenableFuture<?>> monitoredFutures;
    private final List<ListenableFuture<?>> monitoredCallbackFutures;
    private final int MAX_TERMINATION_WAIT = 10;
    private static final int CALLBACK_TIMEOUT = 5;


    @Override
    public void join() throws InterruptedException {
        for (ListenableFuture<?> f : monitoredFutures) {
            try {
                f.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        workerExecutorService.shutdown();
        managerExecutorService.shutdown();
        timeoutExecutorService.shutdownNow();
        for (ListenableFuture<?> f : monitoredCallbackFutures) {
            try {
                f.get(CALLBACK_TIMEOUT, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (TimeoutException e) {
                System.out.println("callback execution timed out");
            }
        }
        callbackExecutorService.shutdown();
        managerExecutorService.awaitTermination(MAX_TERMINATION_WAIT, TimeUnit.SECONDS);
        timeoutExecutorService.awaitTermination(MAX_TERMINATION_WAIT, TimeUnit.SECONDS);
        callbackExecutorService.awaitTermination(MAX_TERMINATION_WAIT, TimeUnit.SECONDS);
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


    public ExecutionEnvironment() {
        this(3);
    }


    public ExecutionEnvironment(int num_threads) {
        this.managerExecutorService = Executors.newFixedThreadPool(Math.max(num_threads - 2, 1));
        this.workerExecutorService = Executors.newFixedThreadPool(Math.max(num_threads - 2, 1));
        this.callbackExecutorService = Executors.newSingleThreadExecutor();
        monitoredFutures = new ArrayList<>();
        monitoredCallbackFutures = new ArrayList<>();
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
        scheduledThreadPoolExecutor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        scheduledThreadPoolExecutor.prestartCoreThread();
        scheduledThreadPoolExecutor.setMaximumPoolSize(num_threads - 2);
        timeoutExecutorService = scheduledThreadPoolExecutor;
    }

    public static <C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> SPECppExecution<C, I, R, F> oneshotExecution(SPECpp<C, I, R, F> specpp, ExecutionParameters executionParameters) {
        SPECppExecution<C, I, R, F> execution;
        try (ExecutionEnvironment ee = new ExecutionEnvironment(3)) {
            execution = ee.execute(specpp, executionParameters);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return execution;
    }

    public <C extends Candidate, I extends CompositionComponent<C>, R extends Result, F extends Result> ListenableFuture<?> addCompletionCallback(SPECppExecution<C, I, R, F> execution, Consumer<SPECppExecution<C, I, R, F>> callback) {
        ListenableFutureTask<?> futureTask = ListenableFutureTask.create(() -> callback.accept(execution), null);
        execution.getMasterComputation().getComputationFuture().addListener(futureTask, callbackExecutorService);
        monitoredCallbackFutures.add(futureTask);
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
                totalCancellationFuture = timeoutExecutorService.schedule(totalCanceller, timeLimits.getTotalTimeLimit()
                                                                                                    .toMillis(), TimeUnit.MILLISECONDS);

            masterComputation.markStarted();
            oc.markStarted();
            specpp.start();

            ScheduledFuture<?> discoveryCancellationFuture = null;
            try {
                workerExecutorService.execute(discoveryFuture);

                if (timeLimits.hasDiscoveryTimeLimit())
                    discoveryCancellationFuture = timeoutExecutorService.schedule(discoveryCanceller, timeLimits.getDiscoveryTimeLimit()
                                                                                                                .toMillis(), TimeUnit.MILLISECONDS);

                if (timeLimits.hasTotalTimeLimit())
                    discoveryFuture.get(timeLimits.getTotalTimeLimit().toMillis(), TimeUnit.MILLISECONDS);
                else discoveryFuture.get();

                oc.markEnded();
            } catch (InterruptedException | ExecutionException | TimeoutException | CancellationException e) {
                specpp.stop();
                oc.markForciblyCancelled();
                oc.markEnded();
                postProcessingFuture.cancel(false);
            } finally {
                if (discoveryCancellationFuture != null) discoveryCancellationFuture.cancel(true);
            }

            osc.markStarted();

            ScheduledFuture<?> postProcessingCancellationFuture = null;
            try {
                workerExecutorService.execute(postProcessingFuture);

                if (timeLimits.hasPostProcessingTimeLimit())
                    postProcessingCancellationFuture = timeoutExecutorService.schedule(postProcessingCanceller, timeLimits.getPostProcessingTimeLimit()
                                                                                                                          .toMillis(), TimeUnit.MILLISECONDS);

                if (timeLimits.hasPostProcessingTimeLimit())
                    postProcessingFuture.get(timeLimits.getPostProcessingTimeLimit().toMillis(), TimeUnit.MILLISECONDS);
                else postProcessingFuture.get();

                osc.markEnded();
                specpp.stop();

            } catch (ExecutionException | InterruptedException | TimeoutException | CancellationException e) {
                osc.markForciblyCancelled();
                osc.markEnded();
                specpp.stop();
                masterComputation.markForciblyCancelled();
            } finally {
                if (postProcessingCancellationFuture != null) postProcessingCancellationFuture.cancel(true);
                if (totalCancellationFuture != null) totalCancellationFuture.cancel(true);
            }

            masterComputation.markEnded();

            return true;
        });
        masterComputation.setComputationFuture(task);

        managerExecutorService.execute(task);
        monitoredFutures.add(task);

        return execution;
    }


}
