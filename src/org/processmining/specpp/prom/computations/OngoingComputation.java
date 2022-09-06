package org.processmining.specpp.prom.computations;

import org.processmining.specpp.supervision.piping.AbstractAsyncAwareObservable;

import java.time.Duration;
import java.time.LocalDateTime;

public class OngoingComputation extends AbstractAsyncAwareObservable<ComputationEvent> {

    private LocalDateTime start, end;
    private Duration timeLimit;
    private boolean gracefullyCancelled, forciblyCancelled;

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        assert this.start == null;
        this.start = start;
        publish(new ComputationStarted(start));
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        assert this.end == null;
        this.end = end;
        publish(new ComputationEnded(end));
    }

    public Duration getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Duration timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Duration calculateRemainingTime() {
        if (timeLimit == null || start == null) return null;
        else return timeLimit.minus(Duration.between(start, LocalDateTime.now()));
    }

    public void setGracefullyCancelled() {
        gracefullyCancelled = true;
        publish(new ComputationCancelled(true));
    }

    public void setForciblyCancelled() {
        forciblyCancelled = true;
        publish(new ComputationCancelled(false));
    }

    public boolean isCancelled() {
        return gracefullyCancelled || forciblyCancelled;
    }
}
