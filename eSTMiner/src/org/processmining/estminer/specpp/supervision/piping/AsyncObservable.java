package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.Observation;

import java.util.concurrent.CompletableFuture;

public interface AsyncObservable<O extends Observation> extends AsyncAwareObservable<O> {

    default void publishAsync(CompletableFuture<O> futureObservation) {
        publishFutureToAsyncObservers(futureObservation);
        publishFutureToNonAsyncObservers(futureObservation);
    }

}
