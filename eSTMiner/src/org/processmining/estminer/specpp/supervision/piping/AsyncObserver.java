package org.processmining.estminer.specpp.supervision.piping;

import org.processmining.estminer.specpp.supervision.observations.Observation;

import java.util.concurrent.CompletableFuture;

public interface AsyncObserver<O extends Observation> extends Observer<O> {

    void observeAsync(CompletableFuture<O> futureObservation);


}
