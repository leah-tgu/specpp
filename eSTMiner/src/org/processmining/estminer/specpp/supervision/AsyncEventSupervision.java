package org.processmining.estminer.specpp.supervision;

import org.processmining.estminer.specpp.supervision.observations.Event;
import org.processmining.estminer.specpp.supervision.piping.AsyncIdentityPipe;
import org.processmining.estminer.specpp.supervision.traits.OneToOne;

import java.util.concurrent.CompletableFuture;

public class AsyncEventSupervision<E extends Event> extends AsyncIdentityPipe<E> implements OneToOne<E, E> {

    @Override
    public void observe(E observation) {
        observeAsync(CompletableFuture.completedFuture(observation));
    }

}
