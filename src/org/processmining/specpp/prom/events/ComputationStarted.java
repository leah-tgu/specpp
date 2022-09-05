package org.processmining.specpp.prom.events;

import java.time.LocalDateTime;

public class ComputationStarted extends ComputationEvent {
    private final LocalDateTime start;

    private ComputationStarted(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getStart() {
        return start;
    }
}
