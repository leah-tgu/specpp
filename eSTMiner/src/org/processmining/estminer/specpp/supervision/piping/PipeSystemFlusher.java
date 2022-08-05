package org.processmining.estminer.specpp.supervision.piping;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PipeSystemFlusher {

    public static void flush(Collection<Observable<?>> observables) {
        Set<Observable<?>> seen = new HashSet<>();
        for (Observable<?> observable : observables) {
            handleObservable(observable, seen);
        }
    }


    private static void handleObservable(Observable<?> observable, Set<Observable<?>> seen) {
        seen.add(observable);
        if (observable instanceof Buffering) ((Buffering) observable).flushBuffer();
        for (Observer<?> observer : observable.getObservers()) {
            if (observer instanceof Observable && !seen.contains(observer)) {
                Observable<?> child = (Observable<?>) observer;
                handleObservable(child, seen);
            }
        }
    }


}
